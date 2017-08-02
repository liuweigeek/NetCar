package com.imagine.scott.netcar.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.bean.Music;
import com.imagine.scott.netcar.fragment.MusicFragment;

import java.util.List;

/**
 * Created by Scott on 15/12/5.
 */
public class MusicsListAdapter extends RecyclerView.Adapter<MusicsListAdapter.ViewHolder> {

    /*private final int mBackground;
    private final TypedValue mTypedValue = new TypedValue();*/

    private MusicFragment musicFragment;
    private List<Music> musics;


    public MusicsListAdapter(MusicFragment musicFragment, List<Music> musics) {
        this.musicFragment = musicFragment;
        this.musics = musics;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View listItem;
        public ViewHolder(View v) {
            super(v);
            listItem = v;
        }
    }

    @Override
    public MusicsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View listItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_music, parent, false);
  //      listItem.setBackgroundResource(mBackground);
        ViewHolder vh = new ViewHolder(listItem);
        return vh;
    }

    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Music music = musics.get(position);

        TextView itemMusicName = (TextView) holder.listItem.findViewById(R.id.item_music_name);
        TextView itemMusicSinger = (TextView) holder.listItem.findViewById(R.id.item_music_singer);

        itemMusicName.setText(music.getTitle());
        itemMusicSinger.setText(music.getSinger());
        holder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicFragment.play(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return musics.size();
    }
}
