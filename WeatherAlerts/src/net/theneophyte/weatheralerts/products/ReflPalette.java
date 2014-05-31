package net.theneophyte.weatheralerts.products;

import android.graphics.Color;

/**
 * Constructs the color palette for a Base Reflectivity products.
 * @author Matt Sutter
 *
 */
public class ReflPalette{

	private static final int ND = 0;
	private static final int TH = 1;
	private static final int DBZ = 0;
	private static final int R_START = 1;
	private static final int G_START = 2;
	private static final int B_START = 3;
	private static final int R_END = 4;
	private static final int G_END = 5;
	private static final int B_END = 6;
	private static final short OPAQUE = 0xFF;
	
	private static final short[][] COLOR_PAL = {
		{10, 164, 164, 255, 100, 100, 192},
		{20,  64, 128, 255,  32,  64, 128},
		{30,   0, 255,   0,   0, 128,   0},
		{40, 255, 255,   0, 255, 128,   0},
		{50, 255,   0,   0, 160,   0,   0},
		{60, 255,   0, 255, 128,   0, 128},
		{70, 255, 255, 255,  -1,  -1,  -1},
		{80, 128, 128, 128,  -1,  -1,  -1}
	};
	
	private static final int STEPS = COLOR_PAL.length;
	private static final int MIN = 0;
	private static final int MAX = STEPS - 1;
	private static final int LEVELS = 256;
	
	private final int[] mColors = new int[LEVELS];
	private final float mMinValue;
	private final float mIncrement;
	
	public ReflPalette(int[] thresh){
		final int min_val = thresh[0];
		final int incr = thresh[1];
		int level;
		
		mMinValue = ((float)min_val)/10.0f;
		mIncrement = ((float)incr)/10.0f;
		
		for (level = 0; level < LEVELS; level++){
			mColors[level] = interpolate(level);
		}
	}
	
	/**
	 * Computes the correct color value for a given reflectivity level.
	 * @param level - Reflectivity level (0 to 255)
	 * @return ARGB color value for that level
	 */
	private int interpolate(int level){
		final int r, g, b;
		final float interp_dbz, start_dbz, end_dbz;
		final float scaling_factor;
		final int r_diff, g_diff, b_diff;
		
		final int start_index;
		int end_index;
		
		if (level == ND || level == TH)
			return Color.TRANSPARENT;
		
		interp_dbz = getValue(level);
		
		if (interp_dbz < COLOR_PAL[MIN][DBZ]){
			return Color.TRANSPARENT;
		}
		
		if (interp_dbz >= COLOR_PAL[MAX][DBZ]){
			r = COLOR_PAL[MAX][R_START];
			g = COLOR_PAL[MAX][G_START];
			b = COLOR_PAL[MAX][B_START];
			return Color.argb(OPAQUE, r, g, b);
		}
		
		for (end_index = MIN + 1; end_index < STEPS; end_index++){
			if (interp_dbz < COLOR_PAL[end_index][DBZ]){
				break;
			}
		}
		
		start_index = end_index - 1;
		
		if (interp_dbz == (float)COLOR_PAL[start_index][DBZ]){
			r = COLOR_PAL[start_index][R_START];
			g = COLOR_PAL[start_index][G_START];
			b = COLOR_PAL[start_index][B_START];
			return Color.argb(OPAQUE, r, g, b);
		}
		
		start_dbz = (float)COLOR_PAL[start_index][DBZ];
		end_dbz = (float)COLOR_PAL[end_index][DBZ];
		
		scaling_factor = (interp_dbz - start_dbz)/(end_dbz - start_dbz);
		
		if (COLOR_PAL[end_index][R_END] < 0){
			r_diff = COLOR_PAL[end_index][R_START] - COLOR_PAL[start_index][R_START];
		}
		else{
			r_diff = COLOR_PAL[start_index][R_END] - COLOR_PAL[start_index][R_START];
		}
		
		if (COLOR_PAL[end_index][G_END] < 0){
			g_diff = COLOR_PAL[end_index][G_START] - COLOR_PAL[start_index][G_START];
		}
		else{
			g_diff = COLOR_PAL[start_index][G_END] - COLOR_PAL[start_index][G_START];
		}
		
		if (COLOR_PAL[end_index][B_END] < 0){
			b_diff = COLOR_PAL[end_index][B_START] - COLOR_PAL[start_index][B_START];
		}
		else{
			b_diff = COLOR_PAL[start_index][B_END] - COLOR_PAL[start_index][B_START];
		}

		r = (int)(r_diff * scaling_factor + COLOR_PAL[start_index][R_START] + 0.5f);
		g = (int)(g_diff * scaling_factor + COLOR_PAL[start_index][G_START] + 0.5f);
		b = (int)(b_diff * scaling_factor + COLOR_PAL[start_index][B_START] + 0.5f);
		
		return Color.argb(OPAQUE, r, g, b);
	}
	
	/**
	 * Compute the dbZ value for a given reflectivity level.
	 * @param level - Reflectivity level
	 * @return - dbZ value
	 */
	public float getValue(int level) {
		return (((float)level - 2.0f) * mIncrement + mMinValue);
	}
	
	public float getIncrement() {
		return mIncrement;
	}

	public float getMinValue() {
		return mMinValue;
	}
}
