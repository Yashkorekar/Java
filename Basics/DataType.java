package Basics;

public class DataType {

	public static void main(String[] args) {
		// 1) Print statements
		System.out.println("=== Print statements ===");
		System.out.print("Hello");
		System.out.print(" ");
		System.out.println("World");
		System.out.printf("Formatted: int=%d, double=%.2f, bool=%b%n", 42, 3.14159, true);

		// 2) Primitive data types (and a couple of reference types)
		System.out.println("\n=== Data types ===");
		byte b = 10;                 // 8-bit signed
		short s = 20;                // 16-bit signed
		int i = 30;                  // 32-bit signed
		long l = 40L;                // 64-bit signed (note the L)
		float f = 2.5f;              // 32-bit float (note the f)
		double d = 2.5;              // 64-bit float
		char c = 'A';                // 16-bit unsigned (UTF-16 code unit)
		boolean ok = true;
		String str = "Java";         // reference type

		System.out.println("byte=" + b + ", short=" + s + ", int=" + i + ", long=" + l); 
		System.out.println("float=" + f + ", double=" + d);
		System.out.println("char='" + c + "' (code=" + (int) c + ")");
		System.out.println("boolean=" + ok + ", String=" + str);

		// 3) Basic operators
		System.out.println("\n=== Operators ===");
		int a = 10;
		int x = 3;

		System.out.println("a + x = " + (a + x));
		System.out.println("a - x = " + (a - x));
		System.out.println("a * x = " + (a * x));
		System.out.println("a / x (int division) = " + (a / x));
		System.out.println("a % x = " + (a % x));
		System.out.println("(double) a / x = " + ((double) a / x));

		int pre = 5;
		System.out.println("pre before: " + pre);
		System.out.println("++pre = " + (++pre));
		System.out.println("pre after ++pre: " + pre);
		System.out.println("pre++ = " + (pre++));
		System.out.println("pre after pre++: " + pre);

		boolean p = true;
		boolean q = false;
		System.out.println("p && q = " + (p && q));
		System.out.println("p || q = " + (p || q));
		System.out.println("!p = " + (!p));

		int bitA = 6;   // 110
		int bitB = 3;   // 011
		System.out.println("bitA & bitB = " + (bitA & bitB));
		System.out.println("bitA | bitB = " + (bitA | bitB));
		System.out.println("bitA ^ bitB = " + (bitA ^ bitB));
		System.out.println("bitA << 1 = " + (bitA << 1));
		System.out.println("bitA >> 1 = " + (bitA >> 1));

		// 4) Casting (widening vs narrowing)
		System.out.println("\n=== Casting ===");
		int smallInt = 123;
		long widenedLong = smallInt;         // widening: implicit
		double widenedDouble = widenedLong;  // widening: implicit
		System.out.println("widenedLong = " + widenedLong);
		System.out.println("widenedDouble = " + widenedDouble);

		double price = 99.99;
		int truncated = (int) price;         // narrowing: explicit (fractional part lost)
		System.out.println("(int) 99.99 = " + truncated);

		int big = 130;
		byte narrowedByte = (byte) big;      // narrowing + overflow (wraps)
		System.out.println("(byte) 130 = " + narrowedByte);

		char letter = (char) 66;             // 66 -> 'B'
		int letterCode = letter;             // char -> int widening
		System.out.println("(char) 66 = '" + letter + "', back to int = " + letterCode);

		// 5) Overflow example with int
		System.out.println("\n=== Overflow ===");
		int max = Integer.MAX_VALUE;
		System.out.println("Integer.MAX_VALUE = " + max);
		System.out.println("MAX_VALUE + 1 = " + (max + 1));

		// 6) Mini exercises (quick interview-style checks)
		System.out.println("\n=== Mini checks ===");
		System.out.println("1) 7 / 2 = " + (7 / 2));
		System.out.println("2) 7 / 2.0 = " + (7 / 2.0));
		System.out.println("3) (int) 'z' = " + (int) 'z');
		System.out.println("4) 'a' + 1 = " + ('a' + 1));
		System.out.println("5) \"a\" + 1 = " + ("a" + 1));
	}
}
