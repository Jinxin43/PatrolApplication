package com.example.dingtu2.myapplication.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dingtu2.myapplication.MainActivity;
import com.example.dingtu2.myapplication.R;
import com.example.dingtu2.myapplication.model.ResultRouteBean;

import java.util.List;


public class PartroAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final Context mContext;
    public   ContentLoadingProgressBar contentLoadingProgressBar;
    private List<ResultRouteBean> mData;
    private final static int TYPE_CONTENT=0;//正常内容
    private final static int TYPE_FOOTER=1;//加载View


    public PartroAdapter(Context context) {
        this.mContext = context;
    }



    @Override
    public int getItemViewType(int position) {
        if (position==mData.size()){
            return TYPE_FOOTER;
        }
        return TYPE_CONTENT;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==TYPE_FOOTER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main_foot, parent, false);
            return new FootViewHolder(view);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item, parent, false);
            MyViewHolder vh = new MyViewHolder(v);
            return vh;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {


        if (getItemViewType(position)==TYPE_FOOTER){

        } else{
            MyViewHolder holders= (MyViewHolder) holder;
            ResultRouteBean bean = mData.get(position);
            if (bean.getType() == 21) {
                holders.mTvPatrolType.setText(mContext.getResources().getString(R.string.ordinary_round));
            } else if (bean.getType() == 22) {
                holders.mTvPatrolType.setText(mContext.getResources().getString(R.string.check_round));
            } else if (bean.getType() == 23) {
                holders.mTvPatrolType.setText(mContext.getResources().getString(R.string.duty_round));
            }
            if(bean.getEndTime()==null||bean.getEndTime().equals("null")){
                holders.mTvTime.setText(bean.getStartTime() );
            }else{
                holders.mTvTime.setText(bean.getStartTime() + "\n" + bean.getEndTime());
            }
            holders.mContent.setText(bean.getPatrolUserName());
            holders.mItemPeople.setText(bean.getName());
            holders.mTvWeather.setText(bean.getWeather());
            holders.mTvDescribe.setText(bean.getDiscription());
            holders.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(mContext, MainActivity.class);
                    intent.putExtra("trackId", mData.get(position).getId());
                    mContext.startActivity(intent);
                    ((Activity) mContext).finish();

                }
            });

        }


    }



    @Override
    public int getItemCount() {
        if (mData != null && mData.size() > 0) {
            return mData.size()+1;
        } else {
            return 0;
        }

    }

    public void setData(List<ResultRouteBean> mRouteBean) {
        this.mData = mRouteBean;
        notifyDataSetChanged();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvPatrolType, mTvTime, mContent,mItemPeople, mTvWeather, mTvDescribe;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTvPatrolType = (TextView) itemView.findViewById(R.id.tv_patrolType);
            mTvTime = (TextView) itemView.findViewById(R.id.item_time);
            mContent = (TextView) itemView.findViewById(R.id.content);
            mItemPeople=(TextView)itemView.findViewById(R.id.item_people);
            mTvWeather=(TextView)itemView.findViewById(R.id.tv_weather);
            mTvDescribe=(TextView)itemView.findViewById(R.id.tv_describe);
        }
    }

    private class FootViewHolder extends RecyclerView.ViewHolder{
        public FootViewHolder(View itemView) {
            super(itemView);
            contentLoadingProgressBar=(ContentLoadingProgressBar)itemView.findViewById(R.id.pb_progress);
            contentLoadingProgressBar.setVisibility(View.GONE);
        }
    }
}
