package cn.authing.guard.util.svg;

public class SVGParseException extends RuntimeException {

    public SVGParseException(String s) {
        super(s);
    }

    public SVGParseException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public SVGParseException(Throwable throwable) {
        super(throwable);
    }
}