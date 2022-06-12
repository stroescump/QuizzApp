package com.irinamihaila.quizzapp.ui.dashboard

import android.os.Bundle
import android.os.Parcelable
import com.irinamihaila.quizzapp.R
import com.irinamihaila.quizzapp.databinding.ActivityDashboardBinding
import com.irinamihaila.quizzapp.models.UserType
import com.irinamihaila.quizzapp.ui.base.BaseActivity
import com.irinamihaila.quizzapp.ui.dashboard.QuizCategory.*
import com.irinamihaila.quizzapp.ui.newquizz.SeeAvailableQuizActivity
import com.irinamihaila.quizzapp.utils.Constants
import com.irinamihaila.quizzapp.utils.SharedPrefsUtils
import com.irinamihaila.quizzapp.utils.viewBinding
import kotlinx.parcelize.Parcelize

class DashboardActivity : BaseActivity() {
    override val binding by viewBinding(ActivityDashboardBinding::inflate)
    private val viewModel by lazy { DashboardViewModel(SharedPrefsUtils(this)) }

    override fun setupListeners() {
        with(binding) {
            arrayOf(
                dashboardItem1 to QuizGeneralKnowledge,
                dashboardItem2 to QuizForeignLanguages,
                dashboardItem3 to QuizScience,
                dashboardItem4 to QuizArt,
                dashboardItem5 to QuizOther
            ).onEach { pair ->
                pair.first.setOnClickListener { provideQuizClickListener(pair.second) }
            }
        }
    }

    override fun initViews() {
        with(binding) {
            containerProfile.tvFirstName.text = getUserFullname()

            tvTitle.text = setTextByUserType(
                R.string.dashboardTitleAuthor,
                R.string.dashboardTitlePlayer
            )

            tvSubtitle.text = setTextByUserType(
                R.string.dashboardAuthorSubtitle,
                R.string.dashboardPlayerSubtitle
            )
        }
    }

    override fun setupObservers() {}

    private fun setTextByUserType(authorRes: Int, playerRes: Int) = when (viewModel.userType) {
        UserType.AUTHOR -> getString(authorRes)
        UserType.PLAYER -> getString(playerRes)
        null -> {
            throw IllegalStateException("QuizUser must have either PLAYER or AUTHOR type.")
        }
    }

    private fun provideQuizClickListener(quizCategory: QuizCategory) {
        navigateTo(
            SeeAvailableQuizActivity::class.java,
            extras = Bundle().also { it.putParcelable(Constants.QUIZ_CATEGORY, quizCategory) }
        )
    }

    private fun getUserFullname() = SharedPrefsUtils(this).getFullName()
}

@Parcelize
sealed class QuizCategory(val name: String) : Parcelable {
    object QuizGeneralKnowledge : QuizCategory("GENERAL_KNOWLEDGE")
    object QuizScience : QuizCategory("SCIENCE")
    object QuizArt : QuizCategory("ART")
    object QuizForeignLanguages : QuizCategory("FOREIGN_LANGUAGES")
    object QuizOther : QuizCategory("OTHER")
}