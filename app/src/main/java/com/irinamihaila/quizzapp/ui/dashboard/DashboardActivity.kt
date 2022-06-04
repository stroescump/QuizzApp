package com.irinamihaila.quizzapp.ui.dashboard

import android.os.Bundle
import android.os.Parcelable
import com.irinamihaila.quizzapp.databinding.ActivityDashboardBinding
import com.irinamihaila.quizzapp.ui.base.BaseActivity
import com.irinamihaila.quizzapp.ui.dashboard.QuizCategory.*
import com.irinamihaila.quizzapp.ui.newquizz.SeeAvailableQuizActivity
import com.irinamihaila.quizzapp.utils.SharedPrefsUtils
import com.irinamihaila.quizzapp.utils.viewBinding
import kotlinx.parcelize.Parcelize

class DashboardActivity : BaseActivity() {
    override val binding by viewBinding(ActivityDashboardBinding::inflate)
    private val viewModel by lazy { DashboardViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setupListeners() {
        with(binding) {
            dashboardItem1.setOnClickListener {
                provideQuizClickListener(QuizGeneralKnowledge)
            }

            dashboardItem2.setOnClickListener {
                provideQuizClickListener(QuizForeignLanguages)
            }

            dashboardItem3.setOnClickListener {
                provideQuizClickListener(QuizScience)
            }

            dashboardItem4.setOnClickListener {
                provideQuizClickListener(QuizArt)
            }
        }
    }

    override fun initViews() {
        with(binding) {
            containerProfile.tvFirstName.text = SharedPrefsUtils(this@DashboardActivity).getFullName()
        }
    }

    override fun setupObservers() {

    }

    private fun provideQuizClickListener(quizCategory: QuizCategory) {
        navigateTo(
            SeeAvailableQuizActivity::class.java,
            extras = Bundle().also { it.putParcelable("data", quizCategory) }
        )
    }
}

@Parcelize
sealed class QuizCategory(val name: String) : Parcelable {
    object QuizGeneralKnowledge : QuizCategory("GENERAL_KNOWLEDGE")
    object QuizScience : QuizCategory("SCIENCE")
    object QuizArt : QuizCategory("ART")
    object QuizForeignLanguages : QuizCategory("FOREIGN_LANGUAGES")
}