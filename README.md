Android 车载应用开发与分析 （3）- HMI 构建 MVVM 架构(Java版)

## 前言

在大多数车载系统应用架构中，一个完整的应用往往会包含三层：

*   **HMI**

Human Machine Interface，显示UI信息，进行人机交互。

*   **Service**

在系统后台进行数据处理，监控数据状态。

*   **SDK**

根据业务逻辑`Service`对外暴露的通信接口，其他模块通过它来完成IPC通信。

当然并不是所有的应用都需要`Service`，只有不能长久的驻留在内存中，且需要监控系统数据和行为的应用才需要`Service`。

举个例子，系统的**OTA**需要一个`Service`在IVI的后台监控云服务或SOA接口的消息，然后完成升级包的下载等。也需要一个`HMI`显示升级的Release Note、确认用户是否同意升级等，这个HMI往往会被归纳在系统设置中。`Service`与`HMI`之间的IPC通信，则需要暴露一个`SDK`来完成，这个其他模块的`HMI`也可以通过这个`SDK`完成与`Service`的IPC通信。

反例则是，**Launcher** 可以长久的驻留在内存，所以它也就不需要`Service`和`SDK`。

**本篇文章主要讲解，如在HMI层中构建一个适合车载系统应用的MVVM架构。本文涉及的源码：https://github.com/linux-link/CarMvvmArch**

## MVVM 架构分层逻辑

**MVVM** 架构的原理以及与MVC&MVP的区别，网上已经有很多相关的优秀文章，这里就不再赘述，本篇文章将聚焦如何车载应用中利用Jetpack组件将 **MVVM** 架构真正落地实现。

![图-1 MVVM架构图](https://upload-images.jianshu.io/upload_images/3146091-ce50fbed5e3c6c19?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

当前的Android应用的**MVVM**架构分层逻辑，都源自图-2 Android官方给出的指导建议，我们也同样基于这套逻辑来实现**MVVM**架构。

![图-2 Android官方推荐的APP架构设计图](https://upload-images.jianshu.io/upload_images/3146091-6f98cccafd230994?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

## 封装适合车载应用 MVVM 框架

**车载应用**相对于**手机应用**来说开发周期和复杂度都要小很多，所以我们封装的重点是**View**层，**ViewModel** 层和 **Model** 层的封装则会简单很多。

### 封装 Model 层

一般来说我们会把访问网络的工具类封装在Model层，但是车载系统应用的 **HMI** 层通常没有访问网络的功能，所以 **Model** 层我们直接留空即可。

```
public abstract class BaseRepository {

}

```

### 封装 ViewModel 层

**VideModel** 层的封装很简单，只需要将Model的实例传入，方便 **ViewModel** 的实现类调用即可。

**封装 ViewModel**

```
public abstract class BaseViewModel<M extends BaseRepository> extends ViewModel {

    protected M mRepository;

    public BaseViewModel(M repository) {
        mRepository = repository;
    }

    public M getRepository() {
        return mRepository;
    }
}

```

**封装 AndroidViewModel**

```
public abstract class BaseAndroidViewModel<M extends BaseRepository> extends AndroidViewModel {

    protected M mRepository;

    public BaseAndroidViewModel(Application application, @Nullable M repository) {
        super(application);
        mRepository = repository;
    }

    public M getRepository() {
        return mRepository;
    }
}

```

### 封装 View 层

**在 View** 层中我们需要引入`Databinding`和`ViewModel`，并且定义出 **View** 的一些实现规范。

在实际使用中，并不是每一个界面都需要使用**MVVM**架构， 所以需要额外封装一个只引入`Databinding`的 **Frangment** 和 **Activity**

**基于 DataBinding 封装****Fragment**

```
public abstract class BaseBindingFragment<V extends ViewDataBinding> extends BaseFragment {

    private static final String TAG = TAG_FWK + BaseBindingFragment.class.getSimpleName();

    protected V mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        LogUtils.logV(TAG, "[onCreateView]");
        if (getLayoutId() == 0) {
            throw new RuntimeException("getLayout() must be not null");
        }
        mBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        mBinding.setLifecycleOwner(this);
        mBinding.executePendingBindings();
        initView();
        return mBinding.getRoot();
    }

    protected abstract void initView();

    @LayoutRes
    protected abstract int getLayoutId();

    public V getBinding() {
        return mBinding;
    }
}

```

**在** **BindingFragment** **的基础上添加 ViewModel**

```
public abstract class BaseMvvmFragment<Vm extends BaseViewModel, V extends ViewDataBinding> extends BaseBindingFragment<V> {

    protected Vm mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initViewModel();
        View view = super.onCreateView(inflater, container, savedInstanceState);
        initObservable(mViewModel);
        if (getViewModelVariable() != 0) {
            mBinding.setVariable(getViewModelVariable(), mViewModel);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadData(getViewModel());
    }

    private void initViewModel() {
        Class<Vm> modelClass;
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            modelClass = (Class<Vm>) ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            modelClass = (Class<Vm>) BaseViewModel.class;
        }
        Object  object = getViewModelOrFactory();
        if (object instanceof ViewModel){
            mViewModel = (Vm) object;
        }else if (object instanceof ViewModelProvider.Factory){
            mViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) object)
                    .get(modelClass);
        }else {
            mViewModel = new ViewModelProvider(this,
                    new ViewModelProvider.NewInstanceFactory()).get(modelClass);
        }
    }

    protected abstract Object getViewModelOrFactory();

    protected abstract int getViewModelVariable();

    protected abstract void initObservable(Vm viewModel);

    protected abstract void loadData(Vm viewModel);

    protected Vm getViewModel() {
        return mViewModel;
    }
}

```

**基于 DataBinding 封装 Activity**

```
public abstract class BaseBindingActivity<V extends ViewDataBinding> extends BaseActivity {

    protected V mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getLayoutId() == 0) {
            throw new RuntimeException("getLayout() must be not null");
        }
        mBinding = DataBindingUtil.setContentView(this, getLayoutId());
        mBinding.setLifecycleOwner(this);
        mBinding.executePendingBindings();
        initView();
    }

    @LayoutRes
    protected abstract int getLayoutId();

    public V getBinding() {
        return mBinding;
    }

    protected abstract void initView();
}

```

**在** **Binding****Activity 的基础上添加 ViewModel**

```
public abstract class BaseMvvmActivity<Vm extends BaseViewModel, V extends ViewDataBinding> extends BaseBindingActivity<V> {

    protected Vm mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initViewModel();
        super.onCreate(savedInstanceState);
        if (getViewModelVariable() != 0) {
            mBinding.setVariable(getViewModelVariable(), mViewModel);
        }
        mBinding.executePendingBindings();
        initObservable(mViewModel);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData(mViewModel);
    }

    private void initViewModel() {
        Class<Vm> modelClass;
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            modelClass = (Class<Vm>) ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            modelClass = (Class<Vm>) BaseViewModel.class;
        }
        Object  object = getViewModelOrFactory();
        if (object instanceof BaseViewModel){
            mViewModel = (Vm) object;
        }else if (object instanceof ViewModelProvider.Factory){
            mViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) object)
                    .get(modelClass);
        }else {
            mViewModel = new ViewModelProvider(this,
                    new ViewModelProvider.NewInstanceFactory()).get(modelClass);
        }
    }

    protected abstract Object getViewModelOrFactory();

    protected abstract int getViewModelVariable();

    protected abstract void initObservable(Vm viewModel);

    protected abstract void loadData(Vm viewModel);

    protected Vm getViewModel() {
        return mViewModel;
    }
}

```

重点解释一下几个abstract的方法

*   **Object getViewModelOrFactory()**

返回ViewModel的实例或ViewModelFactory实例

*   **int getViewModelVariable()**

返回XML中ViewModel的**Variable****Id。例如：BR.viewModel.**

*   **void initObservable(Vm viewModel)**

在此处操作ViewModel中LiveData的。例如：下面这类方法，都应该写在这个方法体里面。目的是为了便于维护

```
viewModel.getTempLive().observe(this, new Observer<String>() {
    @Override
    public void onChanged(String temp) {
        LogUtils.logI(TAG, "[onChanged] " + temp);
    }
});

```

*   **void initView()**

在此处进行初始化UI的操作。例如：初始化RecyclerView，设定ClickListener等等。

*   **void loadData(Vm viewModel)**

在此处使用`ViewModel`进行请求用于初始化UI的数据。

## 基于框架实现MVVM架构

接下来我们基于上面封装的 **MVVM** 框架，来实现一个最基础的 **MVVM** 架构下的demo。

### 定义公共组件

#### 创建 ViewModelFactory

定义`ViewModel`的实例化方式，单一Module下`ViewModel`的创建应该集中在一个`ViewModelFactory`中

```
// default 权限，不对外部公开此类
class AppViewModelFactory implements ViewModelProvider.Factory {

    // 创建 viewModel 实例
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            if (modelClass == HvacViewModel.class) {
                return modelClass.getConstructor(HvacRepository.class, AppExecutors.class)
                        .newInstance(AppInjection.getHvacRepository(), AppExecutors.get());
            } else {
                throw new RuntimeException(modelClass.getSimpleName() + "create failed");
            }
        } catch (NoSuchMethodException | IllegalAccessException
                | InstantiationException | InvocationTargetException exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }
}

```

#### 创建 AppInjection

如果应用中没有使用 **Dagger** 或 **Hilt** 等依赖注入框架，那么为了便于日后的维护，无论是车载应用还是手机应用，都建议定义一个`AppInjection`来将应用中的单例、ViewModel、Repository等实例的获取统一到一个入口程序中。

```
public class AppInjection {

    // ViewModel 工厂
    private final static AppViewModelFactory mViewModelFactory = new AppViewModelFactory();

    public static <T extends ViewModel> T getViewModel(ViewModelStoreOwner store, Class<T> clazz) {
        return new ViewModelProvider(store, mViewModelFactory).get(clazz);
    }

    public static AppViewModelFactory getViewModelFactory() {
        return mViewModelFactory;
    }

    /**
     * 受保护的权限,除了ViewModel，其它模块不应该需要Model层的实例
     *
     * @return {@link HvacRepository}
     */
    protected static HvacRepository getHvacRepository() {
        return new HvacRepository(getHvacManager());
    }

    public static HvacManager getHvacManager() {
        return HvacManager.getInstance();
    }

}

```

### 构建 Model 层

在车载应用中 **Model** 层的主要数据源无外乎 有三种**网络数据源**、**HMI本地数据源**、**IPC（进程间通信）数据源**，其中最常见的是只有**IPC数据源，**三种数据源都有的情况往往会出现在主机厂商自行开发的**车载地图应用**中。所以我们这里只考虑如何基于**IPC数据源**构造**Model**层

定义一个 `XXX``Repository` 继承自 `BaseRepository`，再根据业务需要定义出我们需要使用的接口，这里的`HvacManager`就是**service**提供的用来进行跨进程通信的**IPC-SDK**中的入口。

```
public class HvacRepository extends BaseRepository {

    private static final String TAG = IpcApp.TAG_HVAC + HvacRepository.class.getSimpleName();

    private final HvacManager mHvacManager;
    private HvacCallback mHvacViewModelCallback;

    private final IHvacCallback mHvacCallback = new IHvacCallback() {
        @Override
        public void onTemperatureChanged(double temp) {
            if (mHvacViewModelCallback != null) {
                // 处理远程数据，讲他转换为应用中需要的数据格式或内容
                String value = String.valueOf(temp);
                mHvacViewModelCallback.onTemperatureChanged(value);
            }
        }
    };

    public HvacRepository(HvacManager hvacManager) {
        mHvacManager = hvacManager;
        mHvacManager.registerCallback(mHvacCallback);
    }

    public void clear() {
        mHvacManager.unregisterCallback(mHvacCallback);
    }

    public void requestTemperature() {
        LogUtils.logI(TAG, "[requestTemperature]");
        mHvacManager.requestTemperature();
    }

    public void setTemperature(int temperature) {
        LogUtils.logI(TAG, "[setTemperature] " + temperature);
        mHvacManager.setTemperature(temperature);
    }

    public void setHvacListener(HvacCallback callback) {
        LogUtils.logI(TAG, "[setHvacListener] " + callback);
        mHvacViewModelCallback = callback;
    }

    public void removeHvacListener(HvacCallback callback) {
        LogUtils.logI(TAG, "[removeHvacListener] " + callback);
        mHvacViewModelCallback = null;
    }

}

```

`Repository`通过一个`HvacCallback`将监听的远程数据处理后返回给`ViewModel`。

> 如果应用会与多个不同的模块进行IPC通信，那么建议将这些由不同模块提供的**IPC-SDK**封装在一个Manager中进行统一管理。

### 构建ViewModel

在Jetpack中`ViewModel`的用途是封装界面控制器的数据，以使数据在配置更改后仍然存在。在Android的**MVVM** 架构设计中，`ViewModel`是最关键的一层，通过持有`Repository`的引用来进行外部通信

```
public class HvacViewModel extends BaseViewModel<HvacRepository> {

    private static final String TAG = IpcApp.TAG_HVAC + HvacViewModel.class.getSimpleName();

    private final HvacRepository mRepository;
    // 线程池框架。某些场景，ViewModel访问Repository中的方法可能会需要切换到子线程。
    private final AppExecutors mAppExecutors;
    private MutableLiveData<String> mTempLive;

    private final HvacCallback mHvacCallback = new HvacCallback() {
        @Override
        public void onTemperatureChanged(String temp) {
            LogUtils.logI(TAG, "[onTemperatureChanged] " + temp);
            getTempLive().postValue(temp);
        }
    };

    public HvacViewModel(HvacRepository repository, AppExecutors executors) {
        super(repository);
        mRepository = repository;
        mAppExecutors = executors;
        mRepository.setHvacListener(mHvacCallback);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mRepository.removeHvacListener(mHvacCallback);
        mRepository.release();
    }

    /**
     * 请求页面数据
     */
    public void requestTemperature() {
        mRepository.requestTemperature();
    }

    /**
     * 将温度数据设定到Service中
     *
     * @param view
     */
    public void setTemperature(View view) {
        mRepository.setTemperature(getTempLive().getValue());
    }

    public MutableLiveData<String> getTempLive() {
        if (mTempLive == null) {
            mTempLive = new MutableLiveData<>();
        }
        return mTempLive;
    }
}

```

### 构建View层

最后就是构建View层，一把就是Activity/Fragment和XML。

`HvacActivity`中各个方法含义我们上面封装BaseMvvmActivity的时候已经解释过了，这里不再赘述。

```
public class HvacActivity extends BaseMvvmActivity<HvacViewModel, ActivityHvacBinding> {

    private static final String TAG = IpcApp.TAG_HVAC + HvacActivity.class.getSimpleName();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_hvac;
    }

    @Override
    protected Object getViewModelOrFactory() {
        return AppInjection.getViewModelFactory();
    }

    @Override
    protected int getViewModelVariable() {
        return BR.viewModel;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initObservable(HvacViewModel viewModel) {
        viewModel.getTempLive().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String temp) {
                LogUtils.logI(TAG, "[onChanged] " + temp);
            }
        });
    }

    @Override
    protected void loadData(HvacViewModel viewModel) {
        viewModel.requestTemperature();
    }
}

```

```

<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools">

    <data>

        <variable
            name="viewModel"
            type="com.mvvm.hmi.ipc.ui.HvacViewModel" />
    </data>

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <Button
            android:id="@+id/btn_confirm"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="36dp"
            android:onClick="@{viewModel::setTemperature}"
            android:text="确定"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintHorizontal_bias="0.498"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toBottomOf="@+id/et_temperature" />

        <EditText
            android:id="@+id/et_temperature"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginStart="8dp"
            android:text="@={viewModel.tempLive}"
            app:layout_constraintBottom_toBottomOf="@+id/textView"
            app:layout_constraintStart_toEndOf="@+id/textView"
            app:layout_constraintTop_toTopOf="@+id/textView" />

        <TextView
            android:id="@+id/textView"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginStart="24dp"
            android:layout_marginTop="24dp"
            android:text="Temperature:"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent" />

    </androidx.constraintlayout.widget.ConstraintLayout>
</layout>

```

以上就是如何封装一个适合车载应用使用的 **MVVM** 框架。不知道你有没有发现，在HMI中使用AIDL方法。通常是比较麻烦的。我们需要在HMI与Service完成绑定后，我们才能调用Service中实现的Binder方法。但是示例中我们使用的SDK，并没进行绑定操作，而是直接进行调用。关于如何编写基于AIDL的SDK，就放到下一章再介绍，感谢您的阅读。

本文所涉及的源码请访问：**https://github.com/linux-link/CarMvvmArch**

## 参考资料

[应用架构指南  |  Android 开发者  |  Android Developers](https://developer.android.google.cn/jetpack/guide?hl=zh_cn)
