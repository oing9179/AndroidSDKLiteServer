package oing.webapp.android.sdkliteserver.utils.xmleditor;

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

		public Builder basedOn(String value) {
			this.basedOn = value;
			return this;
		}
	}
}
