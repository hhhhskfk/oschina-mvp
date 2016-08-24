package nucleus.factory;

import nucleus.presenter.Presenter;

/**
 * Created by huangjie08 on 2016/8/16.
 */
public class ReflectionPresenterFactory<P extends Presenter> implements PresenterFactory<P> {

    private Class<P> presenterClass;

    public static <P extends Presenter> ReflectionPresenterFactory<P> fromViewClass(Class<?> viewClass) {
        RequiresPresenter annotation = viewClass.getAnnotation(RequiresPresenter.class);
        Class<P> presenterClass = annotation == null ? null : (Class<P>) annotation.value();
        return presenterClass == null ? null : new ReflectionPresenterFactory<>(presenterClass);
    }

    public ReflectionPresenterFactory(Class<P> presenterClass) {
        this.presenterClass = presenterClass;
    }

    @Override
    public P createPresenter() {
        try {
            return presenterClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
