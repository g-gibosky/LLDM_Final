package SevenZip;

import SevenZip.Compression.LZMA.Decoder;
import SevenZip.Compression.LZMA.Encoder;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.media.session.PlaybackStateCompat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LzmaBench {
    static final int kAdditionalSize = 2097152;
    static final int kCompressedAdditionalSize = 1024;
    static final int kSubBits = 8;

    static class CBenchRandomGenerator {
        public byte[] Buffer = null;
        public int BufferSize;
        int Pos;
        CBitRandomGenerator RG = new CBitRandomGenerator();
        int Rep0;

        public void Set(int bufferSize) {
            this.Buffer = new byte[bufferSize];
            this.Pos = 0;
            this.BufferSize = bufferSize;
        }

        int GetRndBit() {
            return this.RG.GetRnd(1);
        }

        int GetLogRandBits(int numBits) {
            return this.RG.GetRnd(this.RG.GetRnd(numBits));
        }

        int GetOffset() {
            if (GetRndBit() == 0) {
                return GetLogRandBits(4);
            }
            return (GetLogRandBits(4) << 10) | this.RG.GetRnd(10);
        }

        int GetLen1() {
            return this.RG.GetRnd(this.RG.GetRnd(2) + 1);
        }

        int GetLen2() {
            return this.RG.GetRnd(this.RG.GetRnd(2) + 2);
        }

        public void Generate() {
            this.RG.Init();
            this.Rep0 = 1;
            while (this.Pos < this.BufferSize) {
                if (GetRndBit() == 0 || this.Pos < 1) {
                    byte[] bArr = this.Buffer;
                    int i = this.Pos;
                    this.Pos = i + 1;
                    bArr[i] = (byte) this.RG.GetRnd(8);
                } else {
                    int len;
                    if (this.RG.GetRnd(3) == 0) {
                        len = GetLen1() + 1;
                    } else {
                        do {
                            this.Rep0 = GetOffset();
                        } while (this.Rep0 >= this.Pos);
                        this.Rep0++;
                        len = GetLen2() + 2;
                    }
                    int i2 = 0;
                    while (i2 < len && this.Pos < this.BufferSize) {
                        this.Buffer[this.Pos] = this.Buffer[this.Pos - this.Rep0];
                        i2++;
                        this.Pos++;
                    }
                }
            }
        }
    }

    static class CBitRandomGenerator {
        int NumBits;
        CRandomGenerator RG = new CRandomGenerator();
        int Value;

        CBitRandomGenerator() {
        }

        public void Init() {
            this.Value = 0;
            this.NumBits = 0;
        }

        public int GetRnd(int numBits) {
            if (this.NumBits > numBits) {
                int result = this.Value & ((1 << numBits) - 1);
                this.Value >>>= numBits;
                this.NumBits -= numBits;
                return result;
            }
            numBits -= this.NumBits;
            result = this.Value << numBits;
            this.Value = this.RG.GetRnd();
            result |= this.Value & ((1 << numBits) - 1);
            this.Value >>>= numBits;
            this.NumBits = 32 - numBits;
            return result;
        }
    }

    static class CRandomGenerator {
        int A1;
        int A2;

        public CRandomGenerator() {
            Init();
        }

        public void Init() {
            this.A1 = 362436069;
            this.A2 = 521288629;
        }

        public int GetRnd() {
            int i = (36969 * (this.A1 & SupportMenu.USER_MASK)) + (this.A1 >>> 16);
            this.A1 = i;
            i <<= 16;
            int i2 = ((this.A2 & SupportMenu.USER_MASK) * 18000) + (this.A2 >>> 16);
            this.A2 = i2;
            return i ^ i2;
        }
    }

    static class CrcOutStream extends OutputStream {
        public CRC CRC = new CRC();

        CrcOutStream() {
        }

        public void Init() {
            this.CRC.Init();
        }

        public int GetDigest() {
            return this.CRC.GetDigest();
        }

        public void write(byte[] b) {
            this.CRC.Update(b);
        }

        public void write(byte[] b, int off, int len) {
            this.CRC.Update(b, off, len);
        }

        public void write(int b) {
            this.CRC.UpdateByte(b);
        }
    }

    static class MyInputStream extends InputStream {
        byte[] _buffer;
        int _pos;
        int _size;

        public MyInputStream(byte[] buffer, int size) {
            this._buffer = buffer;
            this._size = size;
        }

        public void reset() {
            this._pos = 0;
        }

        public int read() {
            if (this._pos >= this._size) {
                return -1;
            }
            byte[] bArr = this._buffer;
            int i = this._pos;
            this._pos = i + 1;
            return bArr[i] & 255;
        }
    }

    static class MyOutputStream extends OutputStream {
        byte[] _buffer;
        int _pos;
        int _size = this._buffer.length;

        public MyOutputStream(byte[] buffer) {
            this._buffer = buffer;
        }

        public void reset() {
            this._pos = 0;
        }

        public void write(int b) throws IOException {
            if (this._pos >= this._size) {
                throw new IOException("Error");
            }
            byte[] bArr = this._buffer;
            int i = this._pos;
            this._pos = i + 1;
            bArr[i] = (byte) b;
        }

        public int size() {
            return this._pos;
        }
    }

    static class CProgressInfo implements ICodeProgress {
        public long ApprovedStart;
        public long InSize;
        public long Time;

        CProgressInfo() {
        }

        public void Init() {
            this.InSize = 0;
        }

        public void SetProgress(long inSize, long outSize) {
            if (inSize >= this.ApprovedStart && this.InSize == 0) {
                this.Time = System.currentTimeMillis();
                this.InSize = inSize;
            }
        }
    }

    static int GetLogSize(int size) {
        for (int i = 8; i < 32; i++) {
            for (int j = 0; j < 256; j++) {
                if (size <= (1 << i) + (j << (i - 8))) {
                    return (i << 8) + j;
                }
            }
        }
        return 8192;
    }

    static long MyMultDiv64(long value, long elapsedTime) {
        long freq = 1000;
        long elTime = elapsedTime;
        while (freq > 1000000) {
            freq >>>= 1;
            elTime >>>= 1;
        }
        if (elTime == 0) {
            elTime = 1;
        }
        return (value * freq) / elTime;
    }

    static long GetCompressRating(int dictionarySize, long elapsedTime, long size) {
        long t = (long) (GetLogSize(dictionarySize) - 4608);
        return MyMultDiv64(size * (1060 + (((t * t) * 10) >> 16)), elapsedTime);
    }

    static long GetDecompressRating(long elapsedTime, long outSize, long inSize) {
        return MyMultDiv64((220 * inSize) + (20 * outSize), elapsedTime);
    }

    static long GetTotalRating(int dictionarySize, long elapsedTimeEn, long sizeEn, long elapsedTimeDe, long inSizeDe, long outSizeDe) {
        return (GetCompressRating(dictionarySize, elapsedTimeEn, sizeEn) + GetDecompressRating(elapsedTimeDe, inSizeDe, outSizeDe)) / 2;
    }

    static void PrintValue(long v) {
        String s = "" + v;
        for (int i = 0; s.length() + i < 6; i++) {
            System.out.print(" ");
        }
        System.out.print(s);
    }

    static void PrintRating(long rating) {
        PrintValue(rating / 1000000);
        System.out.print(" MIPS");
    }

    static void PrintResults(int dictionarySize, long elapsedTime, long size, boolean decompressMode, long secondSize) {
        long rating;
        PrintValue(MyMultDiv64(size, elapsedTime) / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID);
        System.out.print(" KB/s  ");
        if (decompressMode) {
            rating = GetDecompressRating(elapsedTime, size, secondSize);
        } else {
            rating = GetCompressRating(dictionarySize, elapsedTime, size);
        }
        PrintRating(rating);
    }

    public static int LzmaBenchmark(int numIterations, int dictionarySize) throws Exception {
        if (numIterations <= 0) {
            return 0;
        }
        if (dictionarySize < 262144) {
            System.out.println("\nError: dictionary size for benchmark must be >= 18 (256 KB)");
            return 1;
        }
        System.out.print("\n       Compressing                Decompressing\n\n");
        Encoder encoder = new Encoder();
        Decoder decoder = new Decoder();
        if (encoder.SetDictionarySize(dictionarySize)) {
            int kBufferSize = dictionarySize + 2097152;
            int kCompressedBufferSize = (kBufferSize / 2) + 1024;
            OutputStream propStream = new ByteArrayOutputStream();
            encoder.WriteCoderProperties(propStream);
            decoder.SetDecoderProperties(propStream.toByteArray());
            CBenchRandomGenerator rg = new CBenchRandomGenerator();
            rg.Set(kBufferSize);
            rg.Generate();
            CRC crc = new CRC();
            crc.Init();
            crc.Update(rg.Buffer, 0, rg.BufferSize);
            CProgressInfo progressInfo = new CProgressInfo();
            progressInfo.ApprovedStart = (long) dictionarySize;
            long totalBenchSize = 0;
            long totalEncodeTime = 0;
            long totalDecodeTime = 0;
            long totalCompressedSize = 0;
            MyInputStream inStream = new MyInputStream(rg.Buffer, rg.BufferSize);
            byte[] compressedBuffer = new byte[kCompressedBufferSize];
            MyOutputStream compressedStream = new MyOutputStream(compressedBuffer);
            OutputStream crcOutStream = new CrcOutStream();
            MyInputStream inputCompressedStream = null;
            int compressedSize = 0;
            for (int i = 0; i < numIterations; i++) {
                progressInfo.Init();
                inStream.reset();
                compressedStream.reset();
                encoder.Code(inStream, compressedStream, -1, -1, progressInfo);
                long encodeTime = System.currentTimeMillis() - progressInfo.Time;
                if (i == 0) {
                    compressedSize = compressedStream.size();
                    MyInputStream myInputStream = new MyInputStream(compressedBuffer, compressedSize);
                } else if (compressedSize != compressedStream.size()) {
                    throw new Exception("Encoding error");
                }
                if (progressInfo.InSize == 0) {
                    throw new Exception("Internal ERROR 1282");
                }
                long decodeTime = 0;
                int j = 0;
                while (j < 2) {
                    inputCompressedStream.reset();
                    crcOutStream.Init();
                    long outSize = (long) kBufferSize;
                    long startTime = System.currentTimeMillis();
                    if (decoder.Code(inputCompressedStream, crcOutStream, outSize)) {
                        decodeTime = System.currentTimeMillis() - startTime;
                        if (crcOutStream.GetDigest() != crc.GetDigest()) {
                            throw new Exception("CRC Error");
                        }
                        j++;
                    } else {
                        throw new Exception("Decoding Error");
                    }
                }
                long benchSize = ((long) kBufferSize) - progressInfo.InSize;
                PrintResults(dictionarySize, encodeTime, benchSize, false, 0);
                System.out.print("     ");
                PrintResults(dictionarySize, decodeTime, (long) kBufferSize, true, (long) compressedSize);
                System.out.println();
                totalBenchSize += benchSize;
                totalEncodeTime += encodeTime;
                totalDecodeTime += decodeTime;
                totalCompressedSize += (long) compressedSize;
            }
            System.out.println("---------------------------------------------------");
            PrintResults(dictionarySize, totalEncodeTime, totalBenchSize, false, 0);
            System.out.print("     ");
            PrintResults(dictionarySize, totalDecodeTime, ((long) kBufferSize) * ((long) numIterations), true, totalCompressedSize);
            System.out.println("    Average");
            return 0;
        }
        throw new Exception("Incorrect dictionary size");
    }
}
