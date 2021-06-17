package com.monta.cozy.di.module

import com.monta.cozy.ui.authentication.AuthenticationFragment
import com.monta.cozy.ui.authentication.authen_success.AuthenSuccessFragment
import com.monta.cozy.ui.authentication.birthday_gender.BirthdayGenderFragment
import com.monta.cozy.ui.authentication.email_sign_in.EmailSignInFragment
import com.monta.cozy.ui.authentication.email_sign_up.EmailSignUpFragment
import com.monta.cozy.ui.authentication.name.NameFragment
import com.monta.cozy.ui.authentication.password_sign_in.PasswordSignInFragment
import com.monta.cozy.ui.authentication.password_sign_up.PasswordSignUpFragment
import com.monta.cozy.ui.authentication.unexpected_error.UnexpectedErrorFragment
import com.monta.cozy.ui.dialog.account.AccountDialog
import com.monta.cozy.ui.favorite.FavoriteFragment
import com.monta.cozy.ui.favorite.FavoriteViewModel
import com.monta.cozy.ui.location.LocationFragment
import com.monta.cozy.ui.message.MessageFragment
import com.monta.cozy.ui.message.detail.MessageDetailFragment
import com.monta.cozy.ui.post_room.PostRoomFragment
import com.monta.cozy.ui.room_detail.RoomDetailFragment
import com.monta.cozy.ui.room_detail.detail.DetailFragment
import com.monta.cozy.ui.room_detail.review.ReviewFragment
import com.monta.cozy.ui.search.SearchFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeLocationFragment(): LocationFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchFragment(): SearchFragment

    @ContributesAndroidInjector
    abstract fun contributeSignUpFragment(): AuthenticationFragment

    @ContributesAndroidInjector
    abstract fun contributeNameFragment(): NameFragment

    @ContributesAndroidInjector
    abstract fun contributeBirthdayGenderFragment(): BirthdayGenderFragment

    @ContributesAndroidInjector
    abstract fun contributeEmailSignUpFragment(): EmailSignUpFragment

    @ContributesAndroidInjector
    abstract fun contributeEmailSignInFragment(): EmailSignInFragment

    @ContributesAndroidInjector
    abstract fun contributePasswordSignUpFragment(): PasswordSignUpFragment

    @ContributesAndroidInjector
    abstract fun contributePasswordSignInFragment(): PasswordSignInFragment

    @ContributesAndroidInjector
    abstract fun contributeUnexpectedErrorFragment(): UnexpectedErrorFragment

    @ContributesAndroidInjector
    abstract fun contributeAuthenSuccessFragment(): AuthenSuccessFragment

    @ContributesAndroidInjector
    abstract fun contributeAccountDialog(): AccountDialog

    @ContributesAndroidInjector
    abstract fun contributePostRoomFragment(): PostRoomFragment

    @ContributesAndroidInjector
    abstract fun contributeRoomDetailFragment(): RoomDetailFragment

    @ContributesAndroidInjector
    abstract fun contributeDetailFragment(): DetailFragment

    @ContributesAndroidInjector
    abstract fun contributeReviewFragment(): ReviewFragment

    @ContributesAndroidInjector
    abstract fun contributeMessageDetailFragment(): MessageDetailFragment

    @ContributesAndroidInjector
    abstract fun contributeMessageFragment(): MessageFragment

    @ContributesAndroidInjector
    abstract fun contributeFavoriteFragment(): FavoriteFragment
}