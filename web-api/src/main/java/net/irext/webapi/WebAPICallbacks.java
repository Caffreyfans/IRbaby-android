package net.irext.webapi;

import net.irext.webapi.model.Brand;
import net.irext.webapi.model.Category;
import net.irext.webapi.model.City;
import net.irext.webapi.model.RemoteIndex;
import net.irext.webapi.model.StbOperator;
import net.irext.webapi.model.UserApp;

import java.io.InputStream;
import java.util.List;

/**
 * Filename:       WebAPICallbacks.java
 * Revised:        Date: 2017-07-01
 * Revision:       Revision: 1.0
 * <p>
 * Description:    HTTP Response Callbacks
 * <p>
 * Revision log:
 * 2017-07-01: created by strawmanbobi
 */
public class WebAPICallbacks {

    public interface SignInCallback {
        void onSignInSuccess(UserApp userApp);
        void onSignInFailed();
        void onSignInError();
    }

    public interface ListCategoriesCallback  {
        void onListCategoriesSuccess(List<Category> categories);
        void onListCategoriesFailed();
        void onListCategoriesError();
    }

    public interface ListBrandsCallback {
        void onListBrandsSuccess(List<Brand> brands);
        void onListBrandsFailed();
        void onListBrandsError();
    }

    public interface ListPopularBrandsCallback {
        void onListPopularBrandsSuccess(List<Brand> brands);
        void onListPopularBrandsFailed();
        void onListPopularBrandsError();
    }

    public interface ListPopularCitiesCallback {
        void onListPopularCitiesSuccess(List<City> cities);
        void onListPopularCitiesFailed();
        void onListPopularCitiesError();
    }

    public interface ListProvincesCallback {
        void onListProvincesSuccess(List<City> provinces);
        void onListProvincesFailed();
        void onListProvincesError();
    }

    public interface ListCitiesCallback {
        void onListCitiesSuccess(List<City> cities);
        void onListCitiesFailed();
        void onListCitiesError();
    }

    public interface ListAreasCallback {
        void onListAreasSuccess(List<City> cities);
        void onListAreasFailed();
        void onListAreasError();
    }

    public interface ListOperatersCallback {
        void onListOperatorsSuccess(List<StbOperator> operators);
        void onListOperatorsFailed();
        void onListOperatorsError();
    }

    public interface ListIndexesCallback {
        void onListIndexesSuccess(List<RemoteIndex> indexes);
        void onListIndexesFailed();
        void onListIndexesError();
    }

    public interface DownloadBinCallback {
        void onDownloadBinSuccess(InputStream inputStream);
        void onDownloadBinFailed();
        void onDownloadBinError();
    }
}
