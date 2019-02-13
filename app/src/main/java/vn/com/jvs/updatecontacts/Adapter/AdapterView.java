package vn.com.jvs.updatecontacts.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import vn.com.jvs.updatecontacts.Object.Contracts;
import vn.com.jvs.updatecontacts.R;

/**
 * Created by JVS017
 * on 2018/09/17.
 */
public class AdapterView extends BaseAdapter {
    /**
     * The context.
     */
    private Activity _context;
    private ArrayList<Contracts> _contracts;
    private ViewContract _contractView;

    /**
     * Instantiates a new image adapter.
     *
     * @param localContext the local context
     */
    public AdapterView(Activity localContext, ArrayList<Contracts> contracts) {
        _context = localContext;
        _contracts = contracts;
    }

    /*
    Object view in custom
     */
    private static class ViewContract {
        private TextView _viewName;
        private TextView _viewPhone;
    }

    @Override
    public int getCount() {
        return _contracts.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        AdapterView.ViewContract viewHolder;
        Contracts contracts = _contracts.get(position);

        if (convertView == null) {
            viewHolder = new AdapterView.ViewContract();
            LayoutInflater inflater = LayoutInflater.from(_context);
            convertView = inflater.inflate(R.layout.view_contract, parent, false);

            viewHolder._viewName = convertView.findViewById(R.id.view_name);
            viewHolder._viewPhone = convertView.findViewById(R.id.view_phone);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewContract) convertView.getTag();
        }

        _contractView = viewHolder;
        _contractView._viewName.setText(contracts.get_name());
        _contractView._viewPhone.setText(contracts.get_phone());

        return convertView;
    }

}
