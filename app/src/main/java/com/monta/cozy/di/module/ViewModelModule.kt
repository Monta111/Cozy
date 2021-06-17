package com.monta.cozy.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monta.cozy.ViewModelFactory
import com.monta.cozy.di.ViewModelKey
import com.monta.cozy.ui.ShareViewModel
import com.monta.cozy.ui.authentication.AuthenticationViewModel
import com.monta.cozy.ui.authentication.authen_success.AuthenSuccessViewModel
import com.monta.cozy.ui.authentication.birthday_gender.BirthdayGenderViewModel
import com.monta.cozy.ui.authentication.email_sign_in.EmailSignInViewModel
import com.monta.cozy.ui.authentication.email_sign_up.EmailSignUpViewModel
import com.monta.cozy.ui.authentication.name.NameViewModel
import com.monta.cozy.ui.authentication.password_sign_in.PasswordSignInViewModel
import com.monta.cozy.ui.authentication.password_sign_up.PasswordSignUpViewModel
import com.monta.cozy.ui.authentication.unexpected_error.UnexpectedErrorViewModel
import com.monta.cozy.ui.edit_room.EditRoomViewModel
import com.monta.cozy.ui.favorite.FavoriteViewModel
import com.monta.cozy.ui.location.LocationViewModel
import com.monta.cozy.ui.manage_room.ManageRoomViewModel
import com.monta.cozy.ui.message.MessageViewModel
import com.monta.cozy.ui.message.detail.MessageDetailViewModel
import com.monta.cozy.ui.post_room.PostRoomViewModel
import com.monta.cozy.ui.room_detail.RoomDetailViewModel
import com.monta.cozy.ui.room_detail.detail.DetailViewModel
import com.monta.cozy.ui.room_detail.review.ReviewViewModel
import com.monta.cozy.ui.search.SearchViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun provideViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(ShareViewModel::class)
    abstract fun shareViewModel(viewModel: ShareViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(LocationViewModel::class)
    abstract fun locationViewModel(viewModel: LocationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun searchViewModel(viewModel: SearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AuthenticationViewModel::class)
    abstract fun authenticationViewModel(viewModel: AuthenticationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NameViewModel::class)
    abstract fun nameViewModel(viewModel: NameViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BirthdayGenderViewModel::class)
    abstract fun birthdayGenderViewModel(viewModel: BirthdayGenderViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EmailSignUpViewModel::class)
    abstract fun emailSignUpViewModel(viewModel: EmailSignUpViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EmailSignInViewModel::class)
    abstract fun emailSignInViewModel(viewModel: EmailSignInViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PasswordSignInViewModel::class)
    abstract fun passwordSignInViewModel(viewModel: PasswordSignInViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PasswordSignUpViewModel::class)
    abstract fun passwordSignUpViewModel(viewModel: PasswordSignUpViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UnexpectedErrorViewModel::class)
    abstract fun unexpectedErrorViewModel(viewModel: UnexpectedErrorViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AuthenSuccessViewModel::class)
    abstract fun authenSuccessViewModel(viewModel: AuthenSuccessViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PostRoomViewModel::class)
    abstract fun postRoomViewModel(viewModel: PostRoomViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RoomDetailViewModel::class)
    abstract fun roomDetailViewModel(viewModel: RoomDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DetailViewModel::class)
    abstract fun detailViewModel(viewModel: DetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ReviewViewModel::class)
    abstract fun reviewViewModel(viewModel: ReviewViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MessageDetailViewModel::class)
    abstract fun messsageDetailViewModel(viewModel: MessageDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MessageViewModel::class)
    abstract fun messageViewModel(viewModel: MessageViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FavoriteViewModel::class)
    abstract fun favorieViewModel(viewModel: FavoriteViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ManageRoomViewModel::class)
    abstract fun manageRoomVM(viewModel: ManageRoomViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditRoomViewModel::class)
    abstract fun editRoomVM(viewModel: EditRoomViewModel): ViewModel
}