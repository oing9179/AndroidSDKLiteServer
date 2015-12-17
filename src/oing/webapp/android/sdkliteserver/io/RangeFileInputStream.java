package oing.webapp.android.sdkliteserver.io;

import org.springframework.http.HttpRange;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class RangeFileInputStream extends FileInputStream {
	private final long FILE_RANGE_START;
	private final long FILE_RANGE_END;
	private long ljStreamIndex = -1;

	public RangeFileInputStream(File file, HttpRange range) throws IOException {
		super(file);
		FILE_RANGE_START = range.getRangeStart(file.length());
		FILE_RANGE_END = range.getRangeEnd(file.length());
		ljStreamIndex = FILE_RANGE_START;
		if (skip(FILE_RANGE_START) != FILE_RANGE_START) {
			throw new IOException("Cannot skip " + FILE_RANGE_START + "bytes.");
		}
	}

	@Override
	public int read() throws IOException {
		throw new UnsupportedOperationException("Use read(byte[], int, int) instead.");
	}

	@Override
	public int read(byte[] b) throws IOException {
		return super.read(b);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (ljStreamIndex + len > FILE_RANGE_END) {
			len = (int) (FILE_RANGE_END - ljStreamIndex) + 1;
		}
		if (len <= 0) {
			return -1;
		}
		int liReadedLength = super.read(b, off, len);
		ljStreamIndex += liReadedLength;
		return liReadedLength;
	}
}
