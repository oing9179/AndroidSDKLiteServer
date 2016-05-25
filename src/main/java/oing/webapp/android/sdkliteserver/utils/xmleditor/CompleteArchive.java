package oing.webapp.android.sdkliteserver.utils.xmleditor;

public class CompleteArchive extends Archive {
	public CompleteArchive() {
	}

	private CompleteArchive(Builder builder) {
		super(builder);
	}

	public static class Builder extends Archive.Builder {
		public Builder(RemotePackage remotePackage) {
			super(remotePackage);
		}

		@Override
		public CompleteArchive build() {
			return new CompleteArchive(this);
		}
	}
}
