Android 车载应用开发与分析 （3）- 构建 MVVM 架构(Java版)
前言
更符合车载应用
MVVM 架构原理

构建 Model 层
Model层负责数据的获取，应用的数据源无外乎两种，
本地数据库
网络服务
public interface Webservice {

    @GET("index")
    Call<BaseEntity<MenuEntity>> searchMenu(@Query("key") String key, @Query("word") String word);

}

为了保证数据源的单一性，我们将远程数据源和本地数据合并到一个 Repositroy 中
public class MenuRepository implements IRepository {

    private final LocalService mLocalService;
    private final Webservice mRemoteService;

    public MenuRepository() {
        mRemoteService = HttpService.get().getWebservice();
        mLocalService = LocalService.get();
    }

    @Override
    @WorkerThread
    public List<MenuEntity> searchMenu(String menuName) {
        List<MenuEntity> menuList = new ArrayList<>();
        // 先从网络获取数据
        Call<BaseEntity<MenuEntity>> call = mRemoteService.searchMenu(KEY, menuName);
        try {
            Response<BaseEntity<MenuEntity>> response = call.execute();
            menuList.addAll(response.body().getNewsList());
            saveToLocal(response);
        } catch (IOException e) {
            e.printStackTrace();
            // 从网络中获取数据失败后，再从本地数据库获取缓存数据
            menuList.addAll(mLocalService.searchMenu(menuName));
        }
        return menuList;
    }

    private void saveToLocal(Response<BaseEntity<MenuEntity>> response) {

    }
}

IPC 服务

构建ViewModel
所以，为什么我们需要 ViewModelProvider.Factory ?
我们心中有这样一些疑问，我们不能直接在活动或碎片中将值传入 ViewModel 构造方法中去，我需要写法来设置我们的参数值使其正常工作，这就是为什么我们需要 ViewModelProver.Factory，在一些情况下你可以不使用，但在某些特定情形下，你需要使用 ViewModelProver.Factory。
什么时候使用 ViewModelProvider.Factory
当你的 ViewModel 有依赖项，并且你需要将这些依赖项通过构造方法传入，因此，您可以模拟该依赖项并测试 ViewModel。
什么时候不应该使用 ViewModelProvider.Factory
如果你的 ViewModel 没有依赖项，这时你就不需要去自己创建 ViewModelProvider.Factory。系统自带的方法，注意帮助你创建 ViewModel。
public class MenuViewModel extends ViewModel {

    private final MenuRepository mRepository;

    public MenuViewModel(MenuRepository repository) {
        mRepository = repository;
    }

    public LiveData<List<MenuEntity>> searchMenu(String menuName) {
        MutableLiveData<List<MenuEntity>> liveData= new MutableLiveData<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                liveData.postValue(mRepository.searchMenu(menuName));
            }
        }).start();
        return liveData;
    }
}

// default 权限，不对外部公开此类
class AppViewModelFactory implements ViewModelProvider.Factory {

    // 创建 viewModel 实例
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            return modelClass.getConstructor(MenuRepository.class)
                    .newInstance(AppInjection.getMenuRepository());
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}

// 统一生成APP中 ViewModel & Repository
public class AppInjection {

    private final static AppViewModelFactory mViewModelFactory = new AppViewModelFactory();
    private static volatile MenuRepository mRepository;

    public static <T extends ViewModel> T getViewModel(Class<T> clazz) {
        return mViewModelFactory.create(clazz);
    }

    // 受保护的权限，只对本包中的类公开使用
    protected static MenuRepository getMenuRepository() {
        if (mRepository == null) {
            synchronized (AppInjection.class) {
                if (mRepository == null) {
                    mRepository = new MenuRepository();
                }
            }
        }
        return mRepository;
    }

}

构建View层
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools">

    <data>
        <variable
            name="menu"
            type="com.link.mvvmdemo.bean.biz.MenuEntity" />
    </data>

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        tools:context=".ui.MenuActivity">

        <TextView
            android:id="@+id/typeName"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            android:text="@{menu.typeName}"
            app:layout_constraintHorizontal_bias="0.045"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintRight_toRightOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            tools:text="typename" />

        <TextView
            android:id="@+id/zuofa"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="8dp"
            android:text="@{menu.zuoFa}"
            app:layout_constraintRight_toRightOf="parent"
            app:layout_constraintStart_toStartOf="@+id/typeName"
            app:layout_constraintTop_toBottomOf="@+id/typeName"
            tools:text="zuofa" />

    </androidx.constraintlayout.widget.ConstraintLayout>
</layout>

public class MenuActivity extends AppCompatActivity {

    protected MenuViewModel mMenuViewModel;
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mMenuViewModel = AppInjection.getViewModel(MenuViewModel.class);

        mMenuViewModel.searchMenu("黄瓜")
                .observe(MenuActivity.this, new Observer<List<MenuEntity>>() {
            @Override
            public void onChanged(List<MenuEntity> menuEntities) {
                mBinding.setMenu(menuEntities.get(0));
            }
        });
    }
}


公开网络状态 - 重构 Model 层


使用 Hilt 重构 MVVM 架构

封装一个适合车载应用 MVVM 框架

参考资料
应用架构指南  |  Android 开发者  |  Android Developers
