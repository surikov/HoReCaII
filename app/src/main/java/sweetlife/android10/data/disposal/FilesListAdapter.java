package sweetlife.android10.data.disposal;

import java.util.ArrayList;

import sweetlife.android10.data.common.ZoomListArrayAdapter;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import sweetlife.android10.R;

public class FilesListAdapter extends ZoomListArrayAdapter {

			private ArrayList<String> mFilesList;
			
			public FilesListAdapter( ArrayList<String> filesList ) {

				mFilesList = filesList;
			}

			@Override
			public int getCount() {

				return mFilesList.size();
			}

			@Override
			public Object getItem( int index ) {

				return mFilesList.get(index);
			}

			@Override
			public long getItemId(int index) {

				return index;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {

				FilesViewHolder holder = null;

				if (convertView == null) {

					convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_files, null );
					
					holder = new FilesViewHolder( convertView );

					convertView.setTag(holder);
				} 
				else {

					holder = (FilesViewHolder)convertView.getTag();
				}

				holder.SetValues( mFilesList.get(position) );
				
				return convertView;
			}

			public class FilesViewHolder {

				private TextView mTextFileName = null;

				FilesViewHolder( View row ) {

					mTextFileName = (TextView)row.findViewById(R.id.text_file_name);
				}

				void SetValues( String file ) {

					mTextFileName.setText( file );
					mTextFileName.setTextSize(TypedValue.COMPLEX_UNIT_PX, getRowTextFontSize());
				}
			}
}
