package cn.authing.guard.flow;

public class AuthFlowUIConfig {

    private ContentMode contentMode;

    public ContentMode getContentMode() {
        return contentMode;
    }

    public void setContentMode(ContentMode contentMode) {
        this.contentMode = contentMode;
    }

    public enum ContentMode {
        LEFT,
        MIDDLE
    }

    public static class Builder {

        private ContentMode contentMode;

        public Builder() {

        }

        public Builder setContentMode(ContentMode contentMode) {
            this.contentMode = contentMode;
            return this;
        }

        public AuthFlowUIConfig build() {
            AuthFlowUIConfig config = new AuthFlowUIConfig();
            config.setContentMode(contentMode);
            return config;
        }

    }

}
