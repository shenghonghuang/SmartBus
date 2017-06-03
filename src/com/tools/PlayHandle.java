package com.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.media.MediaPlayer;

public class PlayHandle {
	private MediaPlayer mediaPlayer = new MediaPlayer();
	private List<String> MusicList;
	private int index = 0;
	private static int total = 0;

	public void PlayNext(String StationName) {
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.reset();// 重置为初始状态
		}
		index = 0;
		MusicList = new ArrayList<String>();
		MusicList.clear();
		MusicList.add("Next1.mp3");
		MusicList.add(StationName + ".mp3");
		MusicList.add("Next2.mp3");
		total = 2;
		PlaySounds(MusicList.get(index));
	}

	public void PlayApproach(String StationName) {
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.reset();// 重置为初始状态
		}
		index = 0;
		MusicList = new ArrayList<String>();
		MusicList.clear();
		MusicList.add("Approach1.mp3");
		MusicList.add(StationName + ".mp3");
		MusicList.add("Approach2.mp3");
		total = 2;
		PlaySounds(MusicList.get(index));
	}

	public void PlayNextLast(String StationName) {
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.reset();// 重置为初始状态
		}
		index = 0;
		MusicList = new ArrayList<String>();
		MusicList.clear();
		MusicList.add("NextLast1.mp3");
		MusicList.add("NextLast2.mp3");
		MusicList.add(StationName + ".mp3");
		MusicList.add("NextLast3.mp3");
		total = 3;
		PlaySounds(MusicList.get(index));
	}

	public void PlayApproachLast(String StationName) {
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.reset();// 重置为初始状态
		}
		index = 0;
		MusicList = new ArrayList<String>();
		MusicList.clear();
		MusicList.add("ApproachLast1.mp3");
		MusicList.add("ApproachLast2.mp3");
		MusicList.add(StationName + ".mp3");
		MusicList.add("ApproachLast3.mp3");
		total = 3;
		PlaySounds(MusicList.get(index));
	}

	public void PlayFirst(String First, String Last, String Next) {
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.reset();// 重置为初始状态
		}
		index = 0;
		MusicList = new ArrayList<String>();
		MusicList.clear();
		MusicList.add("First1.mp3");// 传递
		MusicList.add("Number.mp3");
		MusicList.add("First2.mp3");// 本车由
		MusicList.add(First + ".mp3");
		MusicList.add("First3.mp3");// 开往
		MusicList.add(Last + ".mp3");
		MusicList.add("First4.mp3");// 请各位
		MusicList.add("First5.mp3");// 车辆起步
		MusicList.add("First6.mp3");// 下一站
		MusicList.add(Next + ".mp3");
		MusicList.add("First7.mp3");// 要下车的乘客
		total = 10;
		PlaySounds(MusicList.get(index));
	}

	public void PlayInvariable(String Title) {
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.reset();// 重置为初始状态
		}
		PlaySounds(Title + ".mp3");
	}

	private void PlaySounds(String FileTitle) {
		mediaPlayer.reset();// 重置为初始状态
		String path = "/sdcard/SmartBus/sound/" + FileTitle;
		try {
			mediaPlayer.setDataSource(path);
			mediaPlayer.prepare();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mediaPlayer.start();

		mediaPlayer
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {// 播出完毕事件
					@Override
					public void onCompletion(MediaPlayer arg0) {
						next();
					}
				});
	}

	private void next() {
		if (index < total) {
			index = index + 1;
			PlaySounds(MusicList.get(index));
		} else {
			mediaPlayer.reset();
			index = 0;
			total = 0;
		}
	}
}
