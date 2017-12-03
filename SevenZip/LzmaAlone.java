package SevenZip;

import SevenZip.Compression.LZMA.Decoder;
import SevenZip.Compression.LZMA.Encoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class LzmaAlone {

    public static class CommandLine {
        public static final int kBenchmak = 2;
        public static final int kDecode = 1;
        public static final int kEncode = 0;
        public int Algorithm = 2;
        public int Command = -1;
        public int DictionarySize = 8388608;
        public boolean DictionarySizeIsDefined = false;
        public boolean Eos = false;
        public int Fb = 128;
        public boolean FbIsDefined = false;
        public String InFile;
        public int Lc = 3;
        public int Lp = 0;
        public int MatchFinder = 1;
        public int NumBenchmarkPasses = 10;
        public String OutFile;
        public int Pb = 2;

        boolean ParseSwitch(String s) {
            if (s.startsWith("d")) {
                this.DictionarySize = 1 << Integer.parseInt(s.substring(1));
                this.DictionarySizeIsDefined = true;
            } else if (s.startsWith("fb")) {
                this.Fb = Integer.parseInt(s.substring(2));
                this.FbIsDefined = true;
            } else if (s.startsWith("a")) {
                this.Algorithm = Integer.parseInt(s.substring(1));
            } else if (s.startsWith("lc")) {
                this.Lc = Integer.parseInt(s.substring(2));
            } else if (s.startsWith("lp")) {
                this.Lp = Integer.parseInt(s.substring(2));
            } else if (s.startsWith("pb")) {
                this.Pb = Integer.parseInt(s.substring(2));
            } else if (s.startsWith("eos")) {
                this.Eos = true;
            } else if (!s.startsWith("mf")) {
                return false;
            } else {
                String mfs = s.substring(2);
                if (mfs.equals("bt2")) {
                    this.MatchFinder = 0;
                } else if (mfs.equals("bt4")) {
                    this.MatchFinder = 1;
                } else if (!mfs.equals("bt4b")) {
                    return false;
                } else {
                    this.MatchFinder = 2;
                }
            }
            return true;
        }

        public boolean Parse(String[] args) throws Exception {
            int pos = 0;
            boolean switchMode = true;
            for (String s : args) {
                if (s.length() == 0) {
                    return false;
                }
                if (switchMode) {
                    if (s.compareTo("--") == 0) {
                        switchMode = false;
                    } else if (s.charAt(0) == '-') {
                        String sw = s.substring(1).toLowerCase();
                        if (sw.length() == 0) {
                            return false;
                        }
                        try {
                            if (!ParseSwitch(sw)) {
                                return false;
                            }
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    }
                }
                if (pos == 0) {
                    if (s.equalsIgnoreCase("e")) {
                        this.Command = 0;
                    } else if (s.equalsIgnoreCase("d")) {
                        this.Command = 1;
                    } else if (!s.equalsIgnoreCase("b")) {
                        return false;
                    } else {
                        this.Command = 2;
                    }
                } else if (pos == 1) {
                    if (this.Command == 2) {
                        try {
                            this.NumBenchmarkPasses = Integer.parseInt(s);
                            if (this.NumBenchmarkPasses < 1) {
                                return false;
                            }
                        } catch (NumberFormatException e2) {
                            return false;
                        }
                    }
                    this.InFile = s;
                } else if (pos != 2) {
                    return false;
                } else {
                    this.OutFile = s;
                }
                pos++;
            }
            return true;
        }
    }

    static void PrintHelp() {
        System.out.println("\nUsage:  LZMA <e|d> [<switches>...] inputFile outputFile\n  e: encode file\n  d: decode file\n  b: Benchmark\n<Switches>\n  -d{N}:  set dictionary - [0,28], default: 23 (8MB)\n  -fb{N}: set number of fast bytes - [5, 273], default: 128\n  -lc{N}: set number of literal context bits - [0, 8], default: 3\n  -lp{N}: set number of literal pos bits - [0, 4], default: 0\n  -pb{N}: set number of pos bits - [0, 4], default: 2\n  -mf{MF_ID}: set Match Finder: [bt2, bt4], default: bt4\n  -eos:   write End Of Stream marker\n");
    }

    public static void main(String[] args) throws Exception {
        System.out.println("\nLZMA (Java) 4.61  2008-11-23\n");
        if (args.length < 1) {
            PrintHelp();
            return;
        }
        CommandLine params = new CommandLine();
        if (!params.Parse(args)) {
            System.out.println("\nIncorrect command");
        } else if (params.Command == 2) {
            int dictionary = 2097152;
            if (params.DictionarySizeIsDefined) {
                dictionary = params.DictionarySize;
            }
            if (params.MatchFinder > 1) {
                throw new Exception("Unsupported match finder");
            }
            LzmaBench.LzmaBenchmark(params.NumBenchmarkPasses, dictionary);
        } else if (params.Command == 0 || params.Command == 1) {
            File file = new File(params.InFile);
            file = new File(params.OutFile);
            BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(file));
            BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(file));
            boolean eos = false;
            if (params.Eos) {
                eos = true;
            }
            int i;
            if (params.Command == 0) {
                Encoder encoder = new Encoder();
                if (!encoder.SetAlgorithm(params.Algorithm)) {
                    throw new Exception("Incorrect compression mode");
                } else if (!encoder.SetDictionarySize(params.DictionarySize)) {
                    throw new Exception("Incorrect dictionary size");
                } else if (!encoder.SetNumFastBytes(params.Fb)) {
                    throw new Exception("Incorrect -fb value");
                } else if (!encoder.SetMatchFinder(params.MatchFinder)) {
                    throw new Exception("Incorrect -mf value");
                } else if (encoder.SetLcLpPb(params.Lc, params.Lp, params.Pb)) {
                    long fileSize;
                    encoder.SetEndMarkerMode(eos);
                    encoder.WriteCoderProperties(outStream);
                    if (eos) {
                        fileSize = -1;
                    } else {
                        fileSize = file.length();
                    }
                    for (i = 0; i < 8; i++) {
                        outStream.write(((int) (fileSize >>> (i * 8))) & 255);
                    }
                    encoder.Code(inStream, outStream, -1, -1, null);
                } else {
                    throw new Exception("Incorrect -lc or -lp or -pb value");
                }
            }
            byte[] properties = new byte[5];
            if (inStream.read(properties, 0, 5) != 5) {
                throw new Exception("input .lzma file is too short");
            }
            Decoder decoder = new Decoder();
            if (decoder.SetDecoderProperties(properties)) {
                long outSize = 0;
                for (i = 0; i < 8; i++) {
                    int v = inStream.read();
                    if (v < 0) {
                        throw new Exception("Can't read stream size");
                    }
                    outSize |= ((long) v) << (i * 8);
                }
                if (!decoder.Code(inStream, outStream, outSize)) {
                    throw new Exception("Error in data stream");
                }
            }
            throw new Exception("Incorrect stream properties");
            outStream.flush();
            outStream.close();
            inStream.close();
        } else {
            throw new Exception("Incorrect command");
        }
    }
}
