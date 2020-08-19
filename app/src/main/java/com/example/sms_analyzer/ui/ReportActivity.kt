package com.example.sms_analyzer.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.math.MathUtils
import com.example.sms_analyzer.R
import kotlinx.android.synthetic.main.activity_report.*
import org.eazegraph.lib.models.PieModel
import java.lang.Double.sum
import java.util.*
import kotlin.collections.ArrayList


class ReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        val CreditList = intent.getSerializableExtra("CREDIT") as List<*>
        val DebitList = intent.getSerializableExtra("DEBIT") as List<*>


        Log.d("Test-Report", "Credit ${CreditList}")
        Log.d("Test-Report", "Debit $DebitList")

        var creditSum = 0.0F
        var debitSum = 0.0F
        CreditList.forEach {
            creditSum += it.toString().toFloat()
        }

        DebitList.forEach {
            debitSum += it.toString().toFloat()
        }

        tvExpense.text = debitSum.toString()
        tvIncome.text = creditSum.toString()

        piechart.addPieSlice(
            PieModel(
                "Income",
                    creditSum,
                Color.parseColor("#FFA726")
            )
        )
        piechart.addPieSlice(
            PieModel(
                "Expense",
                debitSum,
                Color.parseColor("#66BB6A")
            )
        )


    }
}