package sweetlife.android10.reports;

import java.util.ArrayList;

import sweetlife.android10.data.common.ClientInfo;
import sweetlife.android10.data.common.ContractInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import sweetlife.android10.*;

public class ClientsAndContractsListAdapter extends BaseExpandableListAdapter {

	private Context mContext;	
	private ArrayList<ClientInfo> mClientsList;	

	public ClientsAndContractsListAdapter(Context context ) {

		mContext = context;

		mClientsList = new ArrayList<ClientInfo>();
	}

	public void addItem(ClientInfo info) {

		if( !CheckIfItemExist(info) ) {

			mClientsList.add(info);

			notifyDataSetChanged();
		}
	}

	private boolean CheckIfItemExist(ClientInfo info) {

		boolean exist = false;

		for( int i = 0; i < mClientsList.size(); i++ ) {

			if( mClientsList.get(i).getName().compareToIgnoreCase(info.getName()) == 0 ) {

				exist = true;
			}
		}

		return exist;
	}

	public void removeItem( int groupPosition ) {

		if( groupPosition >= 0 && groupPosition < mClientsList.size() ) {

			mClientsList.remove(groupPosition);

			notifyDataSetChanged();
		}
	}

	@Override
	public boolean areAllItemsEnabled() {

		return true;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {

		return mClientsList.get(groupPosition).getContract(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {

		return childPosition;
	}

	// Return a group view. You can load your custom layout here.
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
			ViewGroup parent) {

		String group = mClientsList.get(groupPosition).getName();

		if( convertView == null ) {

			convertView = LayoutInflater.from(mContext).inflate(R.layout.list_group_element, null);
		}

		TextView textName = (TextView)convertView.findViewById(R.id.text);
		textName.setText(group);

		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
			View convertView, ViewGroup parent) {

		String contract = ((ContractInfo)getChild(groupPosition, childPosition)).getNaimenovanie();

		if( convertView == null ) {

			convertView = LayoutInflater.from(mContext).inflate(R.layout.list_child_element, null);
		}
		TextView textName = (TextView) convertView.findViewById(R.id.text);

		textName.setText(contract);

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {

		return mClientsList.get(groupPosition).getContractsCount();
	}

	@Override
	public Object getGroup(int groupPosition) {

		return mClientsList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {

		return mClientsList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {

		return groupPosition;
	}

	@Override
	public boolean hasStableIds() {

		return true;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {

		return true;
	}
}