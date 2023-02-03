package ghanbari.maziar.notedesk.utils.di

import android.content.ClipboardManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OtherModule {

    @Provides
    @Singleton
    fun provideClipboardManager(@ApplicationContext context: Context) =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager




}