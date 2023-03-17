package sweetlife.android10.utils;

import java.util.UUID;

public class Hex {

	private static final char[] HEX_DIGITS = new char[]{
		'0', '1', '2', '3', '4', '5', '6', '7',
		'8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
	};

	private static final char[] FIRST_CHAR = new char[256];
	private static final char[] SECOND_CHAR = new char[256];

	static {

		for (int i = 0; i < 256; i++) {

			FIRST_CHAR[i] = HEX_DIGITS[(i >> 4) & 0xF];
			SECOND_CHAR[i] = HEX_DIGITS[i & 0xF];
		}
	}

	private static final byte[] DIGITS = new byte['f'+1];
	static {

		for (int i = 0; i <= 'F'; i++) {

			DIGITS[i] = -1;
		}
		for (byte i = 0; i < 10; i++) {

			DIGITS['0' + i] = i;
		}
		for (byte i = 0; i < 6; i++) {

			DIGITS['A' + i] = (byte)(10 + i);
			DIGITS['a' + i] = (byte)(10 + i);
		}
	}

	public static String encodeHex(byte[] array) {

		char[] cArray = new char[array.length * 2];

		for (int i = 0, j = 0; i < array.length; i++) {

			int index = array[i] & 0xFF;

			cArray[j++] = FIRST_CHAR[index];
			cArray[j++] = SECOND_CHAR[index];
		}

		return "x'" + new String(cArray) + "'";
	}

	public static byte[] decodeHexWithPrefix(String hexString) {

		return decodeHex( hexString.substring(2, hexString.length() - 1));
	}

	public static byte[] decodeHex(String hexString) {

		int length = hexString.length();

		if ((length & 0x01) != 0) {

			throw new IllegalArgumentException("Odd number of characters: "+hexString);
		}

		boolean badHex = false;

		byte[] out = new byte[length >> 1];

		for (int i = 0, j = 0; j < length; i++) {

			int c1 = hexString.charAt(j++);

			if (c1 > 'f') {

				badHex = true;
				break;
			}

			final byte d1 = DIGITS[c1];

			if (d1 == -1) {

				badHex = true;
				break;
			}

			int c2 = hexString.charAt(j++);

			if (c2 > 'f') {

				badHex = true;
				break;
			}

			final byte d2 = DIGITS[c2];

			if (d2 == -1) {

				badHex = true;
				break;
			}

			out[i] = (byte) (d1 << 4 | d2);
		}

		if (badHex) {

			throw new IllegalArgumentException("Invalid hexadecimal digit: " + hexString);
		}

		return out;
	}
	
	public static String generateIDRRefString() {
		
		return "x'" + UUID.randomUUID().toString().replaceAll("-", "") + "'";
	}
}
