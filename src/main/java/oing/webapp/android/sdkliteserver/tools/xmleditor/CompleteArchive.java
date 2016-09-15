package oing.webapp.android.sdkliteserver.tools.xmleditor;

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

		@Override
		public Builder size(Long value) {
			super.size(value);
			return this;
		}

		@Override
		public Builder checksum(String value) {
			super.checksum(value);
			return this;
		}

		@Override
		public Builder url(String value) {
			super.url(value);
			return this;
		}

		@Override
		public Builder hostOs(HostOsType value) {
			super.hostOs(value);
			return this;
		}

		@Override
		public Builder hostBits(HostBitsType value) {
			super.hostBits(value);
			return this;
		}

		@Override
		public Builder isExisted(Boolean value) {
			super.isExisted(value);
			return this;
		}
	}
}
