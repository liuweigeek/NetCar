package com.imagine.scott.netcar.fragment;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.activity.MainActivity;
import com.imagine.scott.netcar.adapter.MusicsListAdapter;
import com.imagine.scott.netcar.bean.Music;
import com.imagine.scott.netcar.operation.GetMusicList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicFragment extends BaseFragment implements MediaPlayer.OnErrorListener {

    public static final String NOTIFICATION_ITEM_BUTTON_LAST = "com.imagine.scott.netcar.notification.ServiceReceiver.last";
    public static final String NOTIFICATION_ITEM_BUTTON_PLAY = "com.imagine.scott.netcar.notification.ServiceReceiver.play";
    public static final String NOTIFICATION_ITEM_BUTTON_NEXT = "com.imagine.scott.netcar.notification.ServiceReceiver.next";

    private NotificationManager manager;

    private boolean canPlay = false;

    public static MediaPlayer player = null;
    private AudioManager audioManager;

    private boolean isLoop = false;
    public static int _id = -1;

    private RecyclerView musicListView;
    private MusicsListAdapter musicListAdapter;
    private LinearLayoutManager musicLayoutManager;

    private boolean isAutoPause;

    private TextView musicName;
    private TextView musicSinger;
    private TextView musicInfo;

    private ImageButton musicRewind;
    private ImageButton musicPlay;
    private ImageButton musicFoward;

    private GetMyMusicListTask mAuthTask;

    private List<Music> musics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = (NotificationManager) MainActivity.mainActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        musics = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootview = inflater.inflate(R.layout.fragment_music, container, false);

        musicName = (TextView) rootview.findViewById(R.id.music_name);
        musicSinger = (TextView) rootview.findViewById(R.id.music_singer);
        musicInfo = (TextView) rootview.findViewById(R.id.music_info);

        musicRewind = (ImageButton) rootview.findViewById(R.id.music_rewind);
        musicPlay = (ImageButton) rootview.findViewById(R.id.music_play);
        musicFoward = (ImageButton) rootview.findViewById(R.id.music_foward);

        musicRewind.setOnClickListener(new MyListener());
        musicPlay.setOnClickListener(new MyListener());
        musicFoward.setOnClickListener(new MyListener());

        musicListView = (RecyclerView) rootview.findViewById(R.id.music_recycler_view);
        musicListView.setHasFixedSize(true);
        musicLayoutManager = new LinearLayoutManager(getActivity());
        musicListView.setLayoutManager(musicLayoutManager);
        musicListAdapter = new MusicsListAdapter(MusicFragment.this, musics);

        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        if (mAuthTask == null) {
            mAuthTask = new GetMyMusicListTask();
            mAuthTask.execute();
        }
        return rootview;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!canPlay) {
            if (mAuthTask == null) {
                mAuthTask = new GetMyMusicListTask();
                mAuthTask.execute();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
            player = null;
        }
        MainActivity.mainActivity.unregisterReceiver(phoneStatusReceiver);
    }

    private BroadcastReceiver phoneStatusReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //如果是拨打电话
            if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                player.pause();
                musicPlay.setImageResource(R.drawable.ic_music_play);
                isAutoPause = true;
                String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            } else {
                //如果是来电
                TelephonyManager tm =
                        (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);

                switch (tm.getCallState()) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        player.pause();
                        musicPlay.setImageResource(R.drawable.ic_music_play);
                        isAutoPause = true;
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        player.pause();
                        musicPlay.setImageResource(R.drawable.ic_music_play);
                        isAutoPause = true;
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (isAutoPause) {
                            player.start();
                            musicPlay.setImageResource(R.drawable.ic_music_pause);
                            isAutoPause = false;
                        }
                        break;
                    default:
                }
            }
        }
    };

    private BroadcastReceiver notificaReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(NOTIFICATION_ITEM_BUTTON_LAST)) {
                rewind();
            } else if (action.equals(NOTIFICATION_ITEM_BUTTON_PLAY)) {
                if (player.isPlaying()) {
                    isAutoPause = false;
                    player.pause();
                    musicPlay.setImageResource(R.drawable.ic_music_play);
                } else {
                    try {
                        player.start();
                        musicPlay.setImageResource(R.drawable.ic_music_pause);
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            } else if (action.equals(NOTIFICATION_ITEM_BUTTON_NEXT)) {
                forward();
            }
        }
    };

    public class GetMyMusicListTask extends AsyncTask<Void, Void, List<Music>> {

        @Override
        protected List<Music> doInBackground(Void... params) {
            return GetMusicList.getMusicData(getActivity());
        }

        @Override
        protected void onPostExecute(final List<Music> musics) {
            mAuthTask = null;
            if (musics.size() > 0) {
                musicInfo.setVisibility(View.GONE);
                MusicFragment.this.musics = musics;
                canPlay = true;
                if (player == null) {
                    play(0);
                }
            } else {
                musicInfo.setText("本地没有搜索到音乐");
                musicInfo.setVisibility(View.VISIBLE);
            }
            musicListView.setLayoutManager(musicLayoutManager);
            musicListAdapter = new MusicsListAdapter(MusicFragment.this, musics);
            musicListView.setAdapter(musicListAdapter);
            musicListView.requestLayout();
            IntentFilter phoneStatefilter = new IntentFilter();
            phoneStatefilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
            phoneStatefilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
            MainActivity.mainActivity.registerReceiver(phoneStatusReceiver, phoneStatefilter);

            /*IntentFilter notifiFilter = new IntentFilter();
            notifiFilter.addAction(NOTIFICATION_ITEM_BUTTON_LAST);
            notifiFilter.addAction(NOTIFICATION_ITEM_BUTTON_PLAY);
            notifiFilter.addAction(NOTIFICATION_ITEM_BUTTON_NEXT);
            MainActivity.mainActivity.registerReceiver(notificaReceiver, notifiFilter);*/
        }
    }

/*    private void showCustomView(String name) {
        RemoteViews remoteViews = new RemoteViews(MainActivity.mainActivity.getPackageName(),
                R.layout.notification_music);

        remoteViews.setTextViewText(R.id.notifi_music_name, name);

        Intent buttonPlayIntent = new Intent(NOTIFICATION_ITEM_BUTTON_LAST);
        PendingIntent pendButtonPlayIntent = PendingIntent.getBroadcast(MainActivity.mainActivity, 0, buttonPlayIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.notifi_music_rewind, pendButtonPlayIntent);

        Intent buttonPlayIntent1 = new Intent(NOTIFICATION_ITEM_BUTTON_PLAY);
        PendingIntent pendButtonPlayIntent1 = PendingIntent.getBroadcast(MainActivity.mainActivity, 0, buttonPlayIntent1, 0);
        remoteViews.setOnClickPendingIntent(R.id.notifi_music_play, pendButtonPlayIntent1);

        Intent buttonPlayIntent2 = new Intent(NOTIFICATION_ITEM_BUTTON_NEXT);
        PendingIntent pendButtonPlayIntent2 = PendingIntent.getBroadcast(MainActivity.mainActivity, 0, buttonPlayIntent2, 0);
        remoteViews.setOnClickPendingIntent(R.id.notifi_music_foward, pendButtonPlayIntent2);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.mainActivity);
        builder.setContent(remoteViews)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("music is playing");

        manager.notify(1, builder.build());
    }*/

    private class MyListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (!canPlay) {
                return;
            }
            if (v == musicRewind) {

                if (player != null) {
                    player.release();
                    player = null;
                }
                rewind();
            } else if (v == musicPlay) {
                if (player == null) {
                    play(0);
                } else if (player.isPlaying()) {
                    isAutoPause = false;
                    player.pause();
                    musicPlay.setImageResource(R.drawable.ic_music_play);
                } else {
                    try {
                        player.start();
                        musicPlay.setImageResource(R.drawable.ic_music_pause);
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            } else if (v == musicFoward) {
                if (player != null) {
                    player.release();
                    player = null;
                }
                forward();
            }
        }
    }

    private void rewind() {
        if (_id <= 0) {
            _id = 0;
        } else {
            _id = _id - 1;
        }
        Music m = musics.get(_id);
        musicName.setText(m.getTitle());
        musicSinger.setText(m.getSinger());
        musicPlay.setImageResource(R.drawable.ic_music_pause);

        //showCustomView(m.getTitle());

        String url = m.getUrl();
        Uri myUri = Uri.parse(url);
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            player.setDataSource(getActivity().getApplicationContext(), myUri);
            player.prepare();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.start();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {

                player.reset();
                if (isLoop == true) {
                    _id = _id - 1;
                }
                forward();
            }
        });
    }

    private void forward() {
        if (_id >= musics.size() - 1) {
            _id = musics.size() - 1;
        } else {
            _id = _id + 1;
        }
        Music m = musics.get(_id);
        musicName.setText(m.getTitle());
        musicSinger.setText(m.getSinger());
        musicPlay.setImageResource(R.drawable.ic_music_pause);

        //showCustomView(m.getTitle());

        String url = m.getUrl();
        Uri myUri = Uri.parse(url);
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            player.setDataSource(getActivity().getApplicationContext(), myUri);
            player.prepare();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.start();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                player.reset();
                if (isLoop == true) {
                    _id = _id - 1;
                }
                forward();
            }
        });
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        player.reset();
        Music m = musics.get(0);
        musicName.setText(m.getTitle());
        musicSinger.setText(m.getSinger());

        musicPlay.setImageResource(R.drawable.ic_music_pause);

        String url = m.getUrl();
        Uri myUri = Uri.parse(url);
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            player.setDataSource(getActivity().getApplicationContext(), myUri);
            player.prepare();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.start();
        return false;
    }

    public void play(int id) {

        if (!canPlay) {
            return;
        }

        Music m = musics.get(id);
        musicName.setText(m.getTitle());
        musicSinger.setText(m.getSinger());
        musicPlay.setImageResource(R.drawable.ic_music_pause);

        //showCustomView(m.getTitle());

        if (id != _id) {
            _id = id;
            if (player != null) {
                if (player.isPlaying()) {
                    player.release();
                    player = null;
                }
            }
            String url = m.getUrl();
            Uri myUri = Uri.parse(url);

            player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setWakeMode(getActivity(), PowerManager.PARTIAL_WAKE_LOCK);
            try {
                player.setDataSource(getActivity().getApplicationContext(), myUri);
                player.prepare();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            player.start();
        }
        if (player != null) {
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    player.reset();
                    if (isLoop == true) {
                        _id = _id - 1;
                    }
                    forward();
                }
            });
        }
    }
}
