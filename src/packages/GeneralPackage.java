package packages;

public class GeneralPackage {

    protected PackageType type;
    protected String cmd;
    protected String message;

    public GeneralPackage() {
    }

    public void khoiTao(String cmd, String msg) {
        setCmd(cmd);
        setMessage(msg);
    }

    public void phanTichMessage(String strReceived) {
        // Gói tin dạng: type:chatclient###cmd:start###msg:Hello World!
        // Lấy 3 phần đầu của gói cách nhau bởi ###
        String[] msgSplit = strReceived.split("###", 3);
        if (msgSplit.length != 3) {
            message = cmd ="";
            type = null;
            return;
        }

        // Lấy type từ phần thứ nhất của gói
        String idPkg = msgSplit[0].trim();
        if (idPkg.startsWith("type:")) {
            type = PackageType.valueOf(idPkg.replaceFirst("type:", ""));
        } else {
            type = null;
        }
        // Lấy message từ phần tứ 2 của gói
        String cmdPkg = msgSplit[1].trim();
        if (cmdPkg.startsWith("cmd:")) {
            cmd = cmdPkg.replaceFirst("cmd:", "");
        } else {
            cmd = "";
        }
        // Lấy message từ phần tứ 3 của gói
        String msgPkg = msgSplit[2].trim();
        if (msgPkg.startsWith("msg:")) {
            message = msgPkg.replaceFirst("msg:", "");
        } else {
            message = "";
        }
    }

    @Override
    public String toString() {
        return String.format("type:%s###cmd:%s###msg:%s", getType().toString(), getCmd(), getMessage());
    }

    public PackageType getType() {
        return type;
    }

    public void setType(PackageType type) {
        this.type = type;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
