package org.xwalk.core.extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import org.xwalk.core.extension.ReflectionHelper.MemberInfo;
import org.xwalk.core.extension.ReflectionHelper.MemberType;

public class JsStubGenerator {
    public static final String MSG_TO_CLASS = "postMessageToClass";
    public static final String MSG_TO_EXTENSION = "postMessageToExtension";
    public static final String MSG_TO_OBJECT = "postMessageToObject";
    public static String TAG = "JsStubGenerator";
    String jsHeader = "var v8tools = requireNative(\"v8tools\");\nvar jsStubModule = requireNative(\"jsStub\");\njsStubModule.init(extension, v8tools);\nvar jsStub = jsStubModule.jsStub;\nvar helper = jsStub.createRootStub(exports);\n";
    ReflectionHelper reflection;

    JsStubGenerator(ReflectionHelper extReflection) {
        this.reflection = extReflection;
    }

    String generate() {
        String result = "";
        MemberInfo entry = this.reflection.getEntryPoint();
        if (entry != null) {
            result = generateEntryPoint(entry);
        }
        if (result.length() <= 0) {
            result = this.jsHeader;
        }
        if (this.reflection.getEventList() != null) {
            result = result + generateEventTarget(this.reflection);
        }
        Map<String, MemberInfo> members = this.reflection.getMembers();
        for (String key : members.keySet()) {
            MemberInfo m = (MemberInfo) members.get(key);
            if (!m.isEntryPoint) {
                switch (m.type) {
                    case JS_PROPERTY:
                        result = result + generateProperty(MSG_TO_EXTENSION, m);
                        break;
                    case JS_METHOD:
                        result = result + generateMethod(MSG_TO_EXTENSION, m, true);
                        break;
                    case JS_CONSTRUCTOR:
                        result = result + generateConstructor(m, true);
                        break;
                    default:
                        break;
                }
            }
        }
        return result + "\n";
    }

    String generateEntryPoint(MemberInfo entry) {
        if (entry.type == MemberType.JS_PROPERTY) {
            String funcName = ((Field) entry.accesser).getType().getSimpleName();
            return this.jsHeader + String.format("%s(exports, helper);\n", new Object[]{getPrototypeName(funcName)});
        } else if (entry.type == MemberType.JS_METHOD) {
            return String.format("exports = %s;\n %s\n %s", new Object[]{getInternalName(entry.jsName), this.jsHeader, generateMethod(MSG_TO_EXTENSION, entry, false)});
        } else if (entry.type != MemberType.JS_CONSTRUCTOR) {
            return "";
        } else {
            return String.format("exports = %s;\n %s\n %s", new Object[]{entry.jsName, this.jsHeader, generateConstructor(entry, false)});
        }
    }

    String[] classGenerator(ReflectionHelper targetReflect) {
        String result = "";
        String staticResult = "";
        if (targetReflect.getEventList() != null) {
            String eventStr = generateEventTarget(targetReflect);
            result = result + eventStr;
            staticResult = staticResult + eventStr;
        }
        Map<String, MemberInfo> members = targetReflect.getMembers();
        for (String key : members.keySet()) {
            String memberStr;
            MemberInfo m = (MemberInfo) members.get(key);
            String msgType = m.isStatic ? MSG_TO_CLASS : MSG_TO_OBJECT;
            switch (m.type) {
                case JS_PROPERTY:
                    memberStr = generateProperty(msgType, m);
                    break;
                case JS_METHOD:
                    memberStr = generateMethod(msgType, m, true);
                    break;
                default:
                    memberStr = "";
                    break;
            }
            if (m.isStatic) {
                staticResult = staticResult + memberStr;
            } else {
                result = result + memberStr;
            }
        }
        return new String[]{result, staticResult};
    }

    String destroyBindingObject(ReflectionHelper targetReflect) {
        String result = "exports.destroy = function() {\n";
        for (String key : targetReflect.getMembers().keySet()) {
            result = result + "delete exports[\"" + key + "\"];\n";
        }
        return (((result + "helper.destroy();\n") + "delete exports[\"__stubHelper\"];\n") + "delete exports[\"destroy\"];\n") + "};";
    }

    String generateEventTarget(ReflectionHelper targetReflect) {
        String[] eventList = targetReflect.getEventList();
        if (eventList == null || eventList.length == 0) {
            return "";
        }
        String gen = "jsStub.makeEventTarget(exports);\n";
        for (String e : eventList) {
            gen = gen + "helper.addEvent(\"" + e + "\");\n";
        }
        return gen;
    }

    String generateProperty(String msgType, MemberInfo m) {
        String name = m.jsName;
        return String.format("jsStub.defineProperty(\"%s\", exports, \"%s\", %b);\n", new Object[]{msgType, name, Boolean.valueOf(m.isWritable)});
    }

    String generatePromiseMethod(String msgType, MemberInfo mInfo) {
        String name = mInfo.jsName;
        String wrapArgs = mInfo.wrapArgs.length() > 0 ? mInfo.wrapArgs : "null";
        String wrapReturns = mInfo.wrapReturns.length() > 0 ? mInfo.wrapReturns : "null";
        return String.format("jsStub.addMethodWithPromise(\"%s\", exports, \"%s\", %s, %s);\n", new Object[]{msgType, name, wrapArgs, wrapReturns});
    }

    String getArgString(Method m, boolean withPromise) {
        if (m == null) {
            return "";
        }
        Class<?>[] pTypes = m.getParameterTypes();
        Annotation[][] anns = m.getParameterAnnotations();
        String jsArgs = "";
        int length = withPromise ? pTypes.length - 1 : pTypes.length;
        for (int i = 0; i < length; i++) {
            String pStr = "arg" + i + "_" + pTypes[i].getSimpleName();
            if (jsArgs.length() > 0) {
                jsArgs = jsArgs + ", ";
            }
            jsArgs = jsArgs + pStr;
        }
        return jsArgs;
    }

    String generateMethod(String msgType, MemberInfo mInfo, boolean isMember) {
        if (mInfo.withPromise) {
            return generatePromiseMethod(msgType, mInfo);
        }
        Method m = mInfo.accesser;
        String iName = getInternalName(mInfo.jsName);
        Annotation[][] anns = m.getParameterAnnotations();
        String jsArgs = getArgString(m, mInfo.withPromise);
        return String.format("function %s(%s) {\n" + (!m.getReturnType().equals(Void.TYPE) ? "  return " : "  ") + "helper.invokeNative(\"%s\", \"%s\", [%s], %b);\n" + "};\n", new Object[]{iName, jsArgs, msgType, mInfo.jsName, jsArgs, Boolean.valueOf(!m.getReturnType().equals(Void.TYPE))}) + (isMember ? String.format("exports[\"%s\"] = %s;\n", new Object[]{name, iName}) : "");
    }

    String getInternalName(String name) {
        return "__" + name;
    }

    String getPrototypeName(String funcName) {
        return "__" + funcName + "_prototype";
    }

    String generateConstructor(MemberInfo mInfo, boolean isMember) {
        String memberStr;
        String name = mInfo.jsName;
        String protoFunc = getPrototypeName(name);
        String argStr = getArgString((Method) mInfo.accesser, false);
        String[] classStr = classGenerator(this.reflection.getConstructorReflection(name));
        String protoStr = String.format("function %s(exports, helper){\n%s\n%s\n}\n", new Object[]{protoFunc, classStr[0], destroyBindingObject(targetReflect)});
        String self = String.format("function %s(%s) {\nvar newObject = this;\nvar objectId =\nNumber(helper.invokeNative(\"%s\", \"+%s\", [%s], true));\nif (!objectId) throw \"Error to create instance for constructor:%s.\";\nvar objectHelper = jsStub.getHelper(newObject, helper);\nobjectHelper.objectId = objectId;\nobjectHelper.constructorJsName = \"%s\";\nobjectHelper.registerLifecycleTracker();%s(newObject, objectHelper);\nhelper.addBindingObject(objectId, newObject);}\nhelper.constructors[\"%s\"] = %s;\n", new Object[]{name, argStr, MSG_TO_EXTENSION, name, argStr, name, name, protoFunc, name, name});
        String staticStr = String.format("(function(exports, helper){\n  helper.constructorJsName = \"%s\";\n%s\n})(%s, jsStub.getHelper(%s, helper));\n", new Object[]{name, classStr[1], name, name});
        if (isMember) {
            memberStr = String.format("exports[\"%s\"] = %s;\n", new Object[]{name, name});
        } else {
            memberStr = "";
        }
        return protoStr + self + staticStr + memberStr;
    }
}
