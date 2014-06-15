package net.theneophyte.weatheralerts.products;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.apache.tools.bzip2.CBZip2InputStream;

import android.text.format.Time;

public class BaseReflectivity {

	/**
	 * Number of values for the data level threshold info.
	 */
	private static final int THRESHOLD_VALUES = 16;
	
	private final long mExpirationDate;
	private final int[] mDataLevelThresh;
	private final int mRadialNum;
	private final int mBinNum;
	
	private final byte[][] mBinValues;
	private final float[] mRadialStartAngles;
	private final float[] mRadialAngleDeltas;
	
	private BaseReflectivity(final long expirationDate, final int[] thresh, final int radials, final int bins, 
			final byte[][] binValues, final float[] radialAngles, final float[] radialDeltas){
		mExpirationDate = expirationDate;
		mDataLevelThresh = thresh;
		mRadialNum = radials;
		mBinNum = bins;
		
		mBinValues = new byte[mRadialNum][mBinNum];
		
		mRadialStartAngles = Arrays.copyOf(radialAngles, mRadialNum);
		mRadialAngleDeltas = Arrays.copyOf(radialDeltas, mRadialNum);
		
		for (int radial = 0; radial < mRadialNum; radial++){
			System.arraycopy(binValues[radial], 0, mBinValues[radial], 0, mBinNum);
		}
	}
	
	protected int getRadialNum(){
		return mRadialNum;
	}
	
	protected int getRangeBinNum(){
		return mBinNum;
	}
	
	protected byte[][] getBinValues(){
		return mBinValues;
	}
	
	protected float[] getRadialStartAngles(){
		return mRadialStartAngles;
	}
	
	protected float[] getRadialAngleDeltas(){
		return mRadialAngleDeltas;
	}
	
	protected int getBinValue(int radialIndex, int binIndex){
		return mBinValues[radialIndex][binIndex];
	}
	
	protected float getStartAngle(int radialIndex){
		return mRadialStartAngles[radialIndex];
	}
	
	protected float getAngleDelta(int radialIndex){
		return mRadialAngleDeltas[radialIndex];
	}
	
	protected boolean isExpired(){
		return System.currentTimeMillis() > mExpirationDate;
	}
	
	public static class BaseReflectivityFactory {

		/**
		 * NWS documentation likes to use "halfwords" for the bit lengths, which is just 2 bytes (16 bits).
		 */
		private static final int HALFWORD_BYTES = 2;
		
		/**
		 * WMO header is in human readable format (string) and contains redundant info at any rate.
		 */
		private static final int WMO_HEADER_BYTES = 15 * HALFWORD_BYTES;
		
		/**
		 * Length of the Message Header Block.
		 */
		private static final int MES_HEADER_BYTES = 9 * HALFWORD_BYTES;
		
		/**
		 * Offset from the start of the Message Header Block to the message length info.
		 */
		private static final int MES_LEN_OFFSET = 4 * HALFWORD_BYTES;
		
		/**
		 * Length of the Product Description Block (including the -1 divider).
		 */
		private static final int PROD_DESC_BLOCK_BYTES = 51 * HALFWORD_BYTES;
		
		/**
		 * Offset from the start of the Product Description Block to the "Volume Coverage Pattern" field (including -1 divider).
		 */
		private static final int VCP_OFFSET = 8 * HALFWORD_BYTES;

		/**
		 * Offset from the start of the Product Description Block to the "Product Generation Date" field (including -1 divider).
		 */
		private static final int PROD_GEN_DATE_OFFSET = 14 * HALFWORD_BYTES;
		
		/**
		 * Offset from the start of the Product Description Block to the "Product Generation Time" field (including -1 divider).
		 */
		private static final int PROD_GEN_TIME_OFFSET = 15 * HALFWORD_BYTES;
		
		/**
		 * Offset from the start of the Product Description Block to the "Data Level Threshold" fields (including -1 divider).
		 */
		private static final int DATA_LEVEL_THRESH_OFFSET = 21 * HALFWORD_BYTES;
		
		/**
		 * Offset from the start of the Product Description Block to the "Offset to Symbology" field (including -1 divider).
		 */
		private static final int SYMB_OFFSET_OFFSET = 45 * HALFWORD_BYTES;
		
		/**
		 * Skip the 'BZ' chars in the bzip header. 
		 */
		private static final int BZIP_HEADER_BYTES = 2;
		
		/**
		 * Number of bytes in the Symbology Block header.
		 */
		private static final int SYMB_HEADER_BYTES = 5 * HALFWORD_BYTES;
		
		/**
		 * Number of bytes in the header for a layer of a Symbology Block
		 */
		private static final int SYMB_LAYER_HEADER_BYTES = 3 * HALFWORD_BYTES;
		
		/**
		 * Number of bytes in the header of a Digital Radial Array Data Packet 
		 */
		private static final int DATA_PACKET_HEADER_LEN = 7 * HALFWORD_BYTES;
		
		/**
		 * Offset from the start of a Digital Radial Array Data Packet to the "Index of First Range Bin" field.
		 */
		private static final int RANGE_BIN_START_OFFSET = 2 * HALFWORD_BYTES;
		
		/**
		 * Offset from the start of a Digital Radial Array Data Packet to the "Number of Range Bins" field.
		 */
		private static final int RANGE_BIN_NUM_OFFSET = 3 * HALFWORD_BYTES;
		
		/**
		 * How many bytes we should skip to get from the "Number of Range Bins" field to the "Number of Radials" field.
		 */
		private static final int RADIAL_NUM_SKIP = DATA_PACKET_HEADER_LEN - RANGE_BIN_NUM_OFFSET - HALFWORD_BYTES;
		
		/**
		 * Number of data fields for each radial.
		 */
		private static final int RADIAL_DATUMS = 3;
		private static final int RADIAL_RLE_VALUES = 0;
		private static final int RADIAL_START_ANGLE = 1;
		private static final int RADIAL_ANGLE_DELTA = 2;
		private static final float RADIAL_DIVISOR = 10.0f;
		
		/**
		 * 
		 * @param file
		 * @return
		 * @throws IOException
		 */
		public static BaseReflectivity loadFile(DataInputStream file) throws IOException{
			file.skipBytes(WMO_HEADER_BYTES);
			file.mark(MES_HEADER_BYTES + PROD_DESC_BLOCK_BYTES);
			
			final long messageLength = getMessageLength(file);
			
			final int vcp = getVcp(file);
			final int genDate = getProductGenDate(file);
			final int genTime = getProductGenTime(file);
			
			final long expirationDate = getDate(genDate, genTime) + refreshTime(vcp);
			
			final int[] dataLevelThresh = getDataLevelThresholds(file);
			
			final long symbOffset = getSymbOffset(file);
			
			final CBZip2InputStream symbBlock = decompressSymbBlock(messageLength, symbOffset, file);
			
			skipBytes(symbBlock, SYMB_HEADER_BYTES + SYMB_LAYER_HEADER_BYTES + RANGE_BIN_START_OFFSET);
			
			final int rangeBinStart = readHalfword(symbBlock);
			final int rangeBinNum = readHalfword(symbBlock);
			
			skipBytes(symbBlock, RADIAL_NUM_SKIP);
			
			final int radialNum = readHalfword(symbBlock);
			
			final byte[][] rangeBinValues = new byte[radialNum][rangeBinNum];
			final float[] radialStartAngles = new float[radialNum];
			final float[] radialAngleDeltas = new float[radialNum];
			
			int rleBytes;
			int radialBuffer[] = new int[RADIAL_DATUMS];
			int bin_index;
			for (int radial = 0; radial < radialNum; radial++){
				
				for (int bin = 0; bin < rangeBinNum; bin++){
					rangeBinValues[radial][bin] = 0;
				}
				
				radialBuffer = readHalfwords(symbBlock, RADIAL_DATUMS);
				rleBytes = radialBuffer[RADIAL_RLE_VALUES];
				radialStartAngles[radial] = radialBuffer[RADIAL_START_ANGLE] / RADIAL_DIVISOR;
				radialAngleDeltas[radial] = radialBuffer[RADIAL_ANGLE_DELTA] / RADIAL_DIVISOR;
				
				for (int bin = 0; bin < rleBytes; bin++){
					bin_index = (rangeBinStart + bin) % rangeBinNum;
					Unsigned.putUnsignedByte(rangeBinValues[radial], symbBlock.read(), bin_index); 
				}
				
				// RLE values for each radial always end on a halfword boundary, but the number of RLE values 
				// for a given radial may be an odd number.  If it's odd we need to skip that unused byte.
				if (rleBytes % HALFWORD_BYTES == 1){
					symbBlock.read();
				}
			}
			return new BaseReflectivity(expirationDate, dataLevelThresh, radialNum, rangeBinNum, 
					rangeBinValues, radialStartAngles, radialAngleDeltas);
		}
		
		private static long getMessageLength(DataInputStream file) throws IOException{
			file.reset();
			file.skipBytes(MES_LEN_OFFSET - HALFWORD_BYTES);
			
			return readUnsignedInt(file);
		}
		
		private static int getVcp(DataInputStream file) throws IOException{
			file.reset();
			file.skipBytes(MES_HEADER_BYTES + VCP_OFFSET);

			return file.readUnsignedShort();
		}
		
		private static int getProductGenDate(DataInputStream file) throws IOException{
			file.reset();
			file.skipBytes(MES_HEADER_BYTES + PROD_GEN_DATE_OFFSET);
			
			return file.readUnsignedShort();
		}
		
		private static int getProductGenTime(DataInputStream file) throws IOException{
			file.reset();
			file.skipBytes(MES_HEADER_BYTES + PROD_GEN_TIME_OFFSET);
			
			return (int)readUnsignedInt(file);
		}
		
		private static int[] getDataLevelThresholds(DataInputStream file) throws IOException{
			file.reset();
			file.skipBytes(MES_HEADER_BYTES + DATA_LEVEL_THRESH_OFFSET);

			final int[] thresh = new int[THRESHOLD_VALUES];
			
			for (int i = 0; i < THRESHOLD_VALUES; i++){
				thresh[i] = file.readUnsignedShort();
			}

			return Arrays.copyOf(thresh, THRESHOLD_VALUES);
		}
		
		private static long getSymbOffset(DataInputStream file) throws IOException{
			file.reset();
			file.skipBytes(MES_HEADER_BYTES + SYMB_OFFSET_OFFSET);

			return readUnsignedInt(file);
		}
		
		private static CBZip2InputStream decompressSymbBlock(final long messageLength, final long symbOffset, DataInputStream file) throws IOException{
			file.reset();
			file.skipBytes((int)(symbOffset) + BZIP_HEADER_BYTES);
			
			final byte[] compressedSymbBlock = new byte[(int)(messageLength - symbOffset - BZIP_HEADER_BYTES)];

			file.readFully(compressedSymbBlock);
			
			return new CBZip2InputStream(new ByteArrayInputStream(compressedSymbBlock));
		}
		
		private static long readUnsignedInt(DataInputStream file) throws IOException{
			final int msb = file.readUnsignedShort();
			final int lsb = file.readUnsignedShort();
			
			return (msb << Short.SIZE) + lsb;
		}
		
		private static void skipBytes(CBZip2InputStream is, int bytes){
			for (int i = 0; i < bytes; i++){
				is.read();
			}
		}
		
		private static int[] readHalfwords(CBZip2InputStream data, int halfwords){
			int[] buffer = new int[halfwords];
			
			for (int i = 0; i < halfwords; i++){
				buffer[i] = readHalfword(data);
			}
			
			return buffer;
		}
		
		private static int readHalfword(CBZip2InputStream data){
			final int msb;
			final int lsb;

			msb = data.read();
			lsb = data.read();
			return (msb << Byte.SIZE) + lsb;
		}

		private static long refreshTime(final int vcp){
	    	switch (vcp){
	    	case 12:
	    	case 212:
	    		return (long) (4.5f * 60.0f * 1000.0f);
	    	case 121:
	    		return (long) (6 * 60 * 1000);
	    	case 31:
	    	case 32:
	    		return (long) (10 * 60 * 1000);
	    	case 11:
	    	case 211:
	    	case 21:
	    	case 221:
	    		return (long) (5 * 60 * 1000);	
	    	default: 
	    		throw new IllegalArgumentException("Not a valid VCP value");
	    	}
		}

		/**
		 * Jan 1, 1970 is day 0 in MJD
		 */
		private static final int EPOCH_YEAR = 1970;
		
		private static long getDate(final int modifiedJulianDate, final int secondsSinceMidnight){
			final Time time = new Time(Time.TIMEZONE_UTC);
			
			// Set date to days since Epoch, time to seconds since midnight, and let Time sort out what the actual date is. 
			time.set(secondsSinceMidnight, 0, 0, modifiedJulianDate + 1, 0, EPOCH_YEAR);
			time.normalize(true);
			
			return time.toMillis(true);
		}
	}
}
