package packages;

public class RemoteDesktopPackage extends GeneralPackage{
    public static final PackageType type = PackageType.REMOTE;

    public RemoteDesktopPackage() {
        setType(type);
    }
}
