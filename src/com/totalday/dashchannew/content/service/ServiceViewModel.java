package com.totalday.dashchannew.content.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.lifecycle.ViewModel;

import com.totalday.dashchannew.content.MainApplication;

public class ServiceViewModel<ServiceBinder extends IBinder> extends ViewModel {
	private final Context context = MainApplication.getInstance();
	private final Class<? extends Service> serviceClass;
	private ServiceBinder binder;
	private final ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			handleDisconnected();
			@SuppressWarnings("unchecked")
			ServiceBinder binder = (ServiceBinder) service;
			ServiceViewModel.this.binder = binder;
			onConnected(binder);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			boolean reconnect = binder != null;
			handleDisconnected();
			if (reconnect) {
				connect();
			}
		}
	};
	public ServiceViewModel(Class<? extends Service> serviceClass) {
		this.serviceClass = serviceClass;
		connect();
	}

	private void connect() {
		context.bindService(new Intent(context, serviceClass), connection, Context.BIND_AUTO_CREATE);
	}

	private void handleDisconnected() {
		if (binder != null) {
			ServiceBinder binder = this.binder;
			this.binder = null;
			onDisconnected(binder);
		}
	}

	protected ServiceBinder getBinder() {
		return binder;
	}

	public void onConnected(ServiceBinder binder) {}
	public void onDisconnected(ServiceBinder binder) {}

	@Override
	protected void onCleared() {
		handleDisconnected();
		context.unbindService(connection);
	}
}
