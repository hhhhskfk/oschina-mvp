package nucleus.factory;

import nucleus.presenter.Presenter;

/**
 * Created by huangjie08 on 2016/8/16.
 */
public interface PresenterFactory<P extends Presenter> {

    P createPresenter();
}
