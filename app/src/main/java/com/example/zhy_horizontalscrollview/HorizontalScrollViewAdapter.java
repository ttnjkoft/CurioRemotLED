package com.example.zhy_horizontalscrollview;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class HorizontalScrollViewAdapter
{

	private Context mContext;
	private LayoutInflater mInflater;
	private List<Item> iData;
	public HorizontalScrollViewAdapter(Context context,List<Item> iData)
	{
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
		this.iData=iData;
	}

	public int getCount()
	{
		return iData.size();

	}

	public Object getItem(int position)
	{
		return iData.get(position);
	}

	public long getItemId(int position)
	{
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder = null;
		if (convertView == null)
		{
			viewHolder = new ViewHolder();

			convertView = mInflater.inflate(
					R.layout.activity_index_gallery_item, parent, false);
			viewHolder.mImg = (ImageView) convertView
					.findViewById(R.id.id_index_gallery_item_image);
			viewHolder.mText = (TextView) convertView
					.findViewById(R.id.id_index_gallery_item_text);

			convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.mImg.setImageResource(iData.get(position).getDeviceImage());
		viewHolder.mText.setText(iData.get(position).getDevicename());

		return convertView;
	}

	private class ViewHolder
	{
		ImageView mImg;
		TextView mText;
	}
	public void clearItemImage()
	{
		for (Item item:iData)
		{
			item.setDeviceImage(item.getImage());
		}
	}
}
