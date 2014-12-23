import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;

/**
 * for uncompress tar.gz file
 * 
 * @author 小狼
 * 
 */
public class GZipUncompressUtil {
	String zipfileName = null;

	public GZipUncompressUtil(String fileName) {
		this.zipfileName = fileName;
	}

	/*
	 * 执行入口,rarFileName为需要解压的文件路径(具体到文件),destDir为解压目标路径
	 */
	public static void unTargzFile(String rarFileName, String destDir) {
		GZipUncompressUtil gzip = new GZipUncompressUtil(rarFileName);
		String outputDirectory = destDir;
		File file = new File(outputDirectory);
		if (!file.exists()) {
			file.mkdir();
		}
		gzip.unzipOarFile(outputDirectory);

	}

	public void unzipOarFile(String outputDirectory) {
		FileInputStream fis = null;
		ArchiveInputStream in = null;
		BufferedInputStream bufferedInputStream = null;
		try {
			fis = new FileInputStream(zipfileName);
			GZIPInputStream is = new GZIPInputStream(new BufferedInputStream(fis));
			in = new ArchiveStreamFactory().createArchiveInputStream("tar", is);
			bufferedInputStream = new BufferedInputStream(in);
			TarArchiveEntry entry = (TarArchiveEntry) in.getNextEntry();
			while (entry != null) {
				String name = entry.getName();
				String[] names = name.split("/");
				String fileName = outputDirectory;
				for (int i = 0; i < names.length; i++) {
					String str = names[i];
					fileName = fileName + File.separator + str;
				}
				if (name.endsWith("/")) {
					mkFolder(fileName);
				} else {
					File file = mkFile(fileName);
					BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
					int b;
					while ((b = bufferedInputStream.read()) != -1) {
						bufferedOutputStream.write(b);
					}
					bufferedOutputStream.flush();
					bufferedOutputStream.close();
				}
				entry = (TarArchiveEntry) in.getNextEntry();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ArchiveException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedInputStream != null) {
					bufferedInputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void mkFolder(String fileName) {
		File f = new File(fileName);
		if (!f.exists()) {
			f.mkdir();
		}
	}

	private File mkFile(String fileName) {
		File f = new File(fileName);
		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f;
	}

	public static void main(String[] args) {
		unTargzFile("d:/certificates.tar.gz", "d:/");
	}
}
