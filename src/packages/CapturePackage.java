package packages;

public class CapturePackage extends GeneralPackage{
    public static final PackageType type = PackageType.SCREENSHOT;

    public CapturePackage() {
        setType(type);
    }
}
