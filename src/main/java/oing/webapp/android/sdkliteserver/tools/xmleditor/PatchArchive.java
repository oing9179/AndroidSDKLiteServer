package oing.webapp.android.sdkliteserver.tools.xmleditor;

public class PatchArchive extends Archive {
	private String basedOn;

	public PatchArchive() {
	}

	private PatchArchive(Builder builder) {
		super(builder);
		this.basedOn = builder.basedOn;
	}

	public String getBasedOn() {
		return basedOn;
	}

	public void setBasedOn(String basedOn) {
		this.basedOn = basedOn;
	}

	public static class Builder extends Archive.Builder {
		private String basedOn;

		public Builder(RemotePackage remotePackage) {
			super(remotePackage);
		}

		@Override
		public PatchArchive build() {
			return new PatchArchive(this);
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
		public Builder isFileExisted(Boolean value) {
			super.isFileExisted(value);
			return this;
		}

		@Override
		public Builder fileNameWithPrefix(String value) {
			super.fileNameWithPrefix(value);
			return this;
		}

		public Builder basedOn(String value) {
			this.basedOn = value;
			return this;
		}
	}
}
