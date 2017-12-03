package SevenZip.Compression.LZ;

import android.support.v4.internal.view.SupportMenu;
import java.io.IOException;

public class BinTree extends InWindow {
    private static final int[] CrcTable = new int[256];
    static final int kBT2HashSize = 65536;
    static final int kEmptyHashValue = 0;
    static final int kHash2Size = 1024;
    static final int kHash3Offset = 1024;
    static final int kHash3Size = 65536;
    static final int kMaxValForNormalize = 1073741823;
    static final int kStartMaxLen = 1;
    boolean HASH_ARRAY = true;
    int _cutValue = 255;
    int _cyclicBufferPos;
    int _cyclicBufferSize = 0;
    int[] _hash;
    int _hashMask;
    int _hashSizeSum = 0;
    int _matchMaxLen;
    int[] _son;
    int kFixHashSize = 66560;
    int kMinMatchCheck = 4;
    int kNumHashDirectBytes = 0;

    public void SetType(int numHashBytes) {
        this.HASH_ARRAY = numHashBytes > 2;
        if (this.HASH_ARRAY) {
            this.kNumHashDirectBytes = 0;
            this.kMinMatchCheck = 4;
            this.kFixHashSize = 66560;
            return;
        }
        this.kNumHashDirectBytes = 2;
        this.kMinMatchCheck = 3;
        this.kFixHashSize = 0;
    }

    public void Init() throws IOException {
        super.Init();
        for (int i = 0; i < this._hashSizeSum; i++) {
            this._hash[i] = 0;
        }
        this._cyclicBufferPos = 0;
        ReduceOffsets(-1);
    }

    public void MovePos() throws IOException {
        int i = this._cyclicBufferPos + 1;
        this._cyclicBufferPos = i;
        if (i >= this._cyclicBufferSize) {
            this._cyclicBufferPos = 0;
        }
        super.MovePos();
        if (this._pos == kMaxValForNormalize) {
            Normalize();
        }
    }

    public boolean Create(int historySize, int keepAddBufferBefore, int matchMaxLen, int keepAddBufferAfter) {
        if (historySize > 1073741567) {
            return false;
        }
        this._cutValue = (matchMaxLen >> 1) + 16;
        super.Create(historySize + keepAddBufferBefore, matchMaxLen + keepAddBufferAfter, ((((historySize + keepAddBufferBefore) + matchMaxLen) + keepAddBufferAfter) / 2) + 256);
        this._matchMaxLen = matchMaxLen;
        int cyclicBufferSize = historySize + 1;
        if (this._cyclicBufferSize != cyclicBufferSize) {
            this._cyclicBufferSize = cyclicBufferSize;
            this._son = new int[(cyclicBufferSize * 2)];
        }
        int hs = 65536;
        if (this.HASH_ARRAY) {
            hs = historySize - 1;
            hs |= hs >> 1;
            hs |= hs >> 2;
            hs |= hs >> 4;
            hs = ((hs | (hs >> 8)) >> 1) | SupportMenu.USER_MASK;
            if (hs > 16777216) {
                hs >>= 1;
            }
            this._hashMask = hs;
            hs = (hs + 1) + this.kFixHashSize;
        }
        if (hs != this._hashSizeSum) {
            this._hashSizeSum = hs;
            this._hash = new int[hs];
        }
        return true;
    }

    public int GetMatches(int[] distances) throws IOException {
        int lenLimit;
        int hashValue;
        int i;
        if (this._pos + this._matchMaxLen <= this._streamPos) {
            lenLimit = this._matchMaxLen;
        } else {
            lenLimit = this._streamPos - this._pos;
            if (lenLimit < this.kMinMatchCheck) {
                MovePos();
                return 0;
            }
        }
        int i2 = 0;
        int matchMinPos = this._pos > this._cyclicBufferSize ? this._pos - this._cyclicBufferSize : 0;
        int cur = this._bufferOffset + this._pos;
        int maxLen = 1;
        int hash2Value = 0;
        int hash3Value = 0;
        if (this.HASH_ARRAY) {
            int temp = CrcTable[this._bufferBase[cur] & 255] ^ (this._bufferBase[cur + 1] & 255);
            hash2Value = temp & 1023;
            temp ^= (this._bufferBase[cur + 2] & 255) << 8;
            hash3Value = temp & SupportMenu.USER_MASK;
            hashValue = ((CrcTable[this._bufferBase[cur + 3] & 255] << 5) ^ temp) & this._hashMask;
        } else {
            hashValue = (this._bufferBase[cur] & 255) ^ ((this._bufferBase[cur + 1] & 255) << 8);
        }
        int curMatch = this._hash[this.kFixHashSize + hashValue];
        if (this.HASH_ARRAY) {
            int curMatch2 = this._hash[hash2Value];
            int curMatch3 = this._hash[hash3Value + 1024];
            this._hash[hash2Value] = this._pos;
            this._hash[hash3Value + 1024] = this._pos;
            if (curMatch2 > matchMinPos && this._bufferBase[this._bufferOffset + curMatch2] == this._bufferBase[cur]) {
                i = 0 + 1;
                maxLen = 2;
                distances[0] = 2;
                i2 = i + 1;
                distances[i] = (this._pos - curMatch2) - 1;
            }
            if (curMatch3 > matchMinPos && this._bufferBase[this._bufferOffset + curMatch3] == this._bufferBase[cur]) {
                if (curMatch3 == curMatch2) {
                    i2 -= 2;
                }
                i = i2 + 1;
                maxLen = 3;
                distances[i2] = 3;
                i2 = i + 1;
                distances[i] = (this._pos - curMatch3) - 1;
                curMatch2 = curMatch3;
            }
            if (i2 != 0 && curMatch2 == curMatch) {
                i2 -= 2;
                maxLen = 1;
            }
        }
        this._hash[this.kFixHashSize + hashValue] = this._pos;
        int ptr0 = (this._cyclicBufferPos << 1) + 1;
        int ptr1 = this._cyclicBufferPos << 1;
        int len1 = this.kNumHashDirectBytes;
        int len0 = len1;
        if (!(this.kNumHashDirectBytes == 0 || curMatch <= matchMinPos || this._bufferBase[(this._bufferOffset + curMatch) + this.kNumHashDirectBytes] == this._bufferBase[this.kNumHashDirectBytes + cur])) {
            i = i2 + 1;
            maxLen = this.kNumHashDirectBytes;
            distances[i2] = maxLen;
            i2 = i + 1;
            distances[i] = (this._pos - curMatch) - 1;
        }
        int count = this._cutValue;
        i = i2;
        while (curMatch > matchMinPos) {
            int count2 = count - 1;
            if (count == 0) {
                break;
            }
            int delta = this._pos - curMatch;
            int cyclicPos = (delta <= this._cyclicBufferPos ? this._cyclicBufferPos - delta : (this._cyclicBufferPos - delta) + this._cyclicBufferSize) << 1;
            int pby1 = this._bufferOffset + curMatch;
            int len = Math.min(len0, len1);
            if (this._bufferBase[pby1 + len] == this._bufferBase[cur + len]) {
                do {
                    len++;
                    if (len == lenLimit) {
                        break;
                    }
                } while (this._bufferBase[pby1 + len] == this._bufferBase[cur + len]);
                if (maxLen < len) {
                    i2 = i + 1;
                    maxLen = len;
                    distances[i] = len;
                    i = i2 + 1;
                    distances[i2] = delta - 1;
                    if (len == lenLimit) {
                        this._son[ptr1] = this._son[cyclicPos];
                        this._son[ptr0] = this._son[cyclicPos + 1];
                        i2 = i;
                        break;
                    }
                }
            }
            i2 = i;
            if ((this._bufferBase[pby1 + len] & 255) < (this._bufferBase[cur + len] & 255)) {
                this._son[ptr1] = curMatch;
                ptr1 = cyclicPos + 1;
                curMatch = this._son[ptr1];
                len1 = len;
            } else {
                this._son[ptr0] = curMatch;
                ptr0 = cyclicPos;
                curMatch = this._son[ptr0];
                len0 = len;
            }
            count = count2;
            i = i2;
        }
        int[] iArr = this._son;
        this._son[ptr1] = 0;
        iArr[ptr0] = 0;
        i2 = i;
        MovePos();
        return i2;
    }

    public void Skip(int num) throws IOException {
        do {
            int lenLimit;
            int hashValue;
            if (this._pos + this._matchMaxLen <= this._streamPos) {
                lenLimit = this._matchMaxLen;
            } else {
                lenLimit = this._streamPos - this._pos;
                if (lenLimit < this.kMinMatchCheck) {
                    MovePos();
                    num--;
                }
            }
            int matchMinPos = this._pos > this._cyclicBufferSize ? this._pos - this._cyclicBufferSize : 0;
            int cur = this._bufferOffset + this._pos;
            if (this.HASH_ARRAY) {
                int temp = CrcTable[this._bufferBase[cur] & 255] ^ (this._bufferBase[cur + 1] & 255);
                this._hash[temp & 1023] = this._pos;
                temp ^= (this._bufferBase[cur + 2] & 255) << 8;
                this._hash[(temp & SupportMenu.USER_MASK) + 1024] = this._pos;
                hashValue = ((CrcTable[this._bufferBase[cur + 3] & 255] << 5) ^ temp) & this._hashMask;
            } else {
                hashValue = (this._bufferBase[cur] & 255) ^ ((this._bufferBase[cur + 1] & 255) << 8);
            }
            int curMatch = this._hash[this.kFixHashSize + hashValue];
            this._hash[this.kFixHashSize + hashValue] = this._pos;
            int ptr0 = (this._cyclicBufferPos << 1) + 1;
            int ptr1 = this._cyclicBufferPos << 1;
            int len1 = this.kNumHashDirectBytes;
            int len0 = len1;
            int count = this._cutValue;
            while (curMatch > matchMinPos) {
                int count2 = count - 1;
                if (count == 0) {
                    break;
                }
                int i;
                int delta = this._pos - curMatch;
                if (delta <= this._cyclicBufferPos) {
                    i = this._cyclicBufferPos - delta;
                } else {
                    i = (this._cyclicBufferPos - delta) + this._cyclicBufferSize;
                }
                int cyclicPos = i << 1;
                int pby1 = this._bufferOffset + curMatch;
                int len = Math.min(len0, len1);
                if (this._bufferBase[pby1 + len] == this._bufferBase[cur + len]) {
                    do {
                        len++;
                        if (len == lenLimit) {
                            break;
                        }
                    } while (this._bufferBase[pby1 + len] == this._bufferBase[cur + len]);
                    if (len == lenLimit) {
                        this._son[ptr1] = this._son[cyclicPos];
                        this._son[ptr0] = this._son[cyclicPos + 1];
                        break;
                    }
                }
                if ((this._bufferBase[pby1 + len] & 255) < (this._bufferBase[cur + len] & 255)) {
                    this._son[ptr1] = curMatch;
                    ptr1 = cyclicPos + 1;
                    curMatch = this._son[ptr1];
                    len1 = len;
                } else {
                    this._son[ptr0] = curMatch;
                    ptr0 = cyclicPos;
                    curMatch = this._son[ptr0];
                    len0 = len;
                }
                count = count2;
            }
            int[] iArr = this._son;
            this._son[ptr1] = 0;
            iArr[ptr0] = 0;
            MovePos();
            num--;
        } while (num != 0);
    }

    void NormalizeLinks(int[] items, int numItems, int subValue) {
        for (int i = 0; i < numItems; i++) {
            int value = items[i];
            if (value <= subValue) {
                value = 0;
            } else {
                value -= subValue;
            }
            items[i] = value;
        }
    }

    void Normalize() {
        int subValue = this._pos - this._cyclicBufferSize;
        NormalizeLinks(this._son, this._cyclicBufferSize * 2, subValue);
        NormalizeLinks(this._hash, this._hashSizeSum, subValue);
        ReduceOffsets(subValue);
    }

    public void SetCutValue(int cutValue) {
        this._cutValue = cutValue;
    }

    static {
        for (int i = 0; i < 256; i++) {
            int r = i;
            for (int j = 0; j < 8; j++) {
                if ((r & 1) != 0) {
                    r = (r >>> 1) ^ -306674912;
                } else {
                    r >>>= 1;
                }
            }
            CrcTable[i] = r;
        }
    }
}
