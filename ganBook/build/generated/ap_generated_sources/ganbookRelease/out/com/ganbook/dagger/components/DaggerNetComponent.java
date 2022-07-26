package com.ganbook.dagger.components;

import com.ganbook.dagger.modules.AppModule;
import com.ganbook.dagger.modules.NetModule;
import com.ganbook.dagger.modules.NetModule_ProvideGsonFactory;
import com.ganbook.dagger.modules.NetModule_ProvideRetrofitCommercialFactory;
import com.ganbook.dagger.modules.NetModule_ProvideRetrofitGETFactory;
import com.ganbook.dagger.modules.NetModule_ProvideRetrofitPOSTFactory;
import com.google.gson.Gson;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.Preconditions;
import javax.annotation.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class DaggerNetComponent {
  private DaggerNetComponent() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private NetModule netModule;

    private Builder() {
    }

    /**
     * @deprecated This module is declared, but an instance is not used in the component. This method is a no-op. For more, see https://dagger.dev/unused-modules.
     */
    @Deprecated
    public Builder appModule(AppModule appModule) {
      Preconditions.checkNotNull(appModule);
      return this;
    }

    public Builder netModule(NetModule netModule) {
      this.netModule = Preconditions.checkNotNull(netModule);
      return this;
    }

    public NetComponent build() {
      Preconditions.checkBuilderRequirement(netModule, NetModule.class);
      return new NetComponentImpl(netModule);
    }
  }

  private static final class NetComponentImpl implements NetComponent {
    private final NetComponentImpl netComponentImpl = this;

    private Provider<Gson> provideGsonProvider;

    private Provider<Retrofit> provideRetrofitPOSTProvider;

    private Provider<Retrofit> provideRetrofitGETProvider;

    private Provider<Retrofit> provideRetrofitCommercialProvider;

    private NetComponentImpl(NetModule netModuleParam) {

      initialize(netModuleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final NetModule netModuleParam) {
      this.provideGsonProvider = DoubleCheck.provider(NetModule_ProvideGsonFactory.create(netModuleParam));
      this.provideRetrofitPOSTProvider = DoubleCheck.provider(NetModule_ProvideRetrofitPOSTFactory.create(netModuleParam, provideGsonProvider));
      this.provideRetrofitGETProvider = DoubleCheck.provider(NetModule_ProvideRetrofitGETFactory.create(netModuleParam, provideGsonProvider));
      this.provideRetrofitCommercialProvider = DoubleCheck.provider(NetModule_ProvideRetrofitCommercialFactory.create(netModuleParam, provideGsonProvider));
    }

    @Override
    public Retrofit retrofitPOST() {
      return provideRetrofitPOSTProvider.get();
    }

    @Override
    public Retrofit retrofitGET() {
      return provideRetrofitGETProvider.get();
    }

    @Override
    public Retrofit retrofitCommercial() {
      return provideRetrofitCommercialProvider.get();
    }
  }
}
