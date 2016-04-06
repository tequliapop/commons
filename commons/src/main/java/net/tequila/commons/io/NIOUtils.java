package net.tequila.commons.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

/**
 * 通用的NIO操作工具类。
 * 
 * Q:BSIZE
 * 
 * @author ChenCheng
 *
 */
public class NIOUtils {
	private static final int BSIZE = 4096;

	/**
	 * 读取指定文件的内容。
	 * 
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public static String read(String name) {
		StringBuilder sb = new StringBuilder();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(name);
			FileChannel fc = fis.getChannel();
			ByteBuffer bb = ByteBuffer.allocate(BSIZE);
			while (fc.read(bb) != -1) {
				bb.flip(); // Prepare for writing
				// decode using this system's default Charset
				String encoding = System.getProperty("file.encoding");
				Charset charset = Charset.forName(encoding);
				sb.append(charset.decode(bb));
				bb.clear(); // Prepare for reading
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(fis);
		}
		return sb.toString();
	}

	/*public static String read(String name) throws IOException {
		FileInputStream fis = new FileInputStream(name);
		FileChannel fc = fis.getChannel();
		ByteBuffer buff = ByteBuffer.allocate(BSIZE);
		StringBuilder sb = new StringBuilder();
		while (fc.read(buff) != -1) {
			buff.flip(); // Prepare for writing
			while (buff.hasRemaining()) {
				sb.append((char) buff.get());
			}
			buff.clear(); // Prepare for reading
		}
		// fc.close();
		fis.close();
		return sb.toString();
	}*/

	/**
	 * 为指定的文件写入指定的内容。
	 * 
	 * @param name
	 * @param content
	 * @throws IOException
	 */
	public static void write(String name, String content) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(name);
			FileChannel fc = fos.getChannel();
			fc.write(ByteBuffer.wrap(content.getBytes()));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(fos);
		}
	}

	/**
	 * 为指定的文件追加指定的内容。
	 * 
	 * @param name
	 * @param content
	 * @throws IOException
	 */
	public static void writeAppend(String name, String content) {
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(name, "rw");
			FileChannel fc = raf.getChannel();
			fc.position(fc.size()); // Move to the end
			fc.write(ByteBuffer.wrap(content.getBytes()));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			IOUtils.closeQuietly(raf);
		}
	}

	/**
	 * 复制文件（使用通道transfer）。
	 * 
	 * @param source
	 * @param dest
	 * @throws IOException
	 */
	public static void copy(String source, String dest) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(source);
			fos = new FileOutputStream(dest);
			FileChannel in = fis.getChannel(), out = fos.getChannel();
			in.transferTo(0, in.size(), out);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			IOUtils.closeQuietly(fos);
			IOUtils.closeQuietly(fis);
		}
	}

	/**
	 * 复制文件（使用通道读写）。
	 * 
	 * @param source
	 * @param dest
	 * @throws IOException
	 */
	/*public static void copy(String source, String dest) throws IOException {
		FileInputStream fis = new FileInputStream(source);
		FileOutputStream fos = new FileOutputStream(dest);
		FileChannel in = fis.getChannel(), out = fos.getChannel();
		ByteBuffer buff = ByteBuffer.allocate(BSIZE);
		while (in.read(buff) != -1) {
			buff.flip(); // Prepare for writing
			out.write(buff);
			buff.clear(); // Prepare for reading
		}
		fos.close();
		fis.close();
	}*/

	/**
	 * 读取CharBuffer中的所有内容。
	 * 
	 * @param buff
	 * @return
	 * @throws IOException
	 */
	public static String read(CharBuffer buff) throws IOException {
		StringBuilder sb = new StringBuilder();
		while (buff.hasRemaining()) {
			sb.append(buff.get());
		}
		return sb.toString();
	}

	/**
	 * 交换CharBuffer中的相邻字符。
	 * 
	 * @param buff
	 */
	public static void symmetricScramble(CharBuffer buff) {
		while (buff.hasRemaining()) {
			buff.mark();
			char c1 = buff.get();
			char c2 = buff.get();
			buff.reset();
			buff.put(c2).put(c1);
		}
	}

	public static void main(String[] args) throws IOException {
		String file = "data.txt";
		String content = "Some text 一些文本";
		write(file, content);
		writeAppend(file, content);
		System.out.println(read(file));

		System.out.println();
		String file1 = "src/main/java/net/tequila/commons/io/NIOUtils.java";
		String file2 = "test.txt";
		copy(file1, file2);
		System.out.println(read(file2));
	}
}
