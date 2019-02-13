package vn.com.jvs.updatecontacts.Object;

/**
 * Created by JVS017
 * on 2018/09/17.
 */
public class Contracts {
    private String _name = "";
    private String _phone = "";
    private String _id = "";

    public String get_type() {
        return _type;
    }

    public void set_type(String _type) {
        this._type = _type;
    }

    private String _type = "";

    public Contracts() {
        _name = "";
        _phone = "";
        _id = "";
        _type = "";
    }

    public Contracts(String name, String phone, String id, String type) {
        this._name = name;
        this._phone = phone;
        this._id = id;
        this._type = type;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_phone() {
        return _phone;
    }

    public void set_phone(String _phone) {
        this._phone = _phone;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
