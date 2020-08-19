package com.example.sms_analyzer.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sms_analyzer.R
import com.example.sms_analyzer.adapter.ReadSmsAdapter
import com.example.sms_analyzer.model.SmsData
import kotlinx.android.synthetic.main.activity_main.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class MainActivity : AppCompatActivity() {

    private val READ_SMS_CODE: Int = 1
    var regEx: Pattern = Pattern.compile("(?i)(?:(?:RS|INR|MRP)\\.?\\s?)(\\d+(:?\\,\\d+)?(\\,\\d+)?(\\.\\d{1,2})?)")

    var finalCreditAmount = ""
    var finalDebitAmount = ""

    var CreditList : ArrayList<String> = ArrayList()
    var DebitList : ArrayList<String> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Check Permission for Read SMS
        checkPermission()

    }
    fun checkPermission(){

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED){
            makeRequest()
        }else{
            ReadSms()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS),READ_SMS_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when(requestCode){
            READ_SMS_CODE -> {
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){

                    Toast.makeText(this,
                        R.string.permission_denied,Toast.LENGTH_LONG).show()
                }else{
                    ReadSms()
                }
            }
        }

    }

    @SuppressLint("WrongConstant")
    private fun ReadSms() {
        //getting recyclerview from xml
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        //adding a layoutmanager
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)


        //Default - Credited
        switch1.text = "Total Income"
        val adapter = ReadSmsAdapter(SetCreditedSms())
        recyclerView.adapter = adapter

        ReadSmsAdapter(SetDebitedSms())
        //Switch Change
        switch1.setOnCheckedChangeListener { compoundButton, isChecked ->
            if(isChecked){
                //Credited
                CreditList.clear()
                switch1.text = "Total Income"
                val adapter = ReadSmsAdapter(SetCreditedSms())
                recyclerView.adapter = adapter

            }else{
                //Debited
                DebitList.clear()
                switch1.text = "Total Expense"
                val adapter = ReadSmsAdapter(SetDebitedSms())
                recyclerView.adapter = adapter

            }
        }
    }

    private fun SetCreditedSms() : ArrayList<SmsData>{

        var CreditedList = ArrayList<SmsData>()

        val uriSms: Uri = Uri.parse("content://sms/inbox")
        val cursor: Cursor? = contentResolver.query(uriSms, arrayOf("_id", "address", "date", "body"), null, null, null)

        cursor!!.moveToFirst()
        while (cursor.moveToNext()) {
            val address: String = cursor.getString(1)
            val body: String = cursor.getString(3)

            if(body.contains("Credited")){
                println("======&gt; Mobile number =&gt; $address")
                println("=====&gt; SMS Text =&gt; $body")

                for (i in body.indices) {

                    val data = body as CharSequence
                    //String with Rs.
                    val regEx = Pattern.compile("(?:inr|Rs.)+[\\s]*[0-9+[\\,]*+[0-9]*]+[\\.]*[0-9]+")
                    val m: Matcher = regEx.matcher(data)
                    if (m.find()) {
                        Log.e("Test", "Amount" + m.group(0))
                        var amount: String = m.group(0).replace("inr", "")
                        amount = amount.replace("rs".toRegex(), "")
                        amount = amount.replace("inr".toRegex(), "")
                        amount = amount.replace(" ".toRegex(), "")
                        amount = amount.replace(",".toRegex(), "")

                        finalCreditAmount = amount.removeRange(0,3)
                        CreditList.add(finalCreditAmount)
                        Log.e("Test", "finalCreditAmount $CreditList")
                        break

                    }
                }

                CreditedList.add(SmsData(address, body))

            }
        }
        return CreditedList
    }
    private fun SetDebitedSms() : ArrayList<SmsData>{

        var DebitedList = ArrayList<SmsData>()

        val uriSms: Uri = Uri.parse("content://sms/inbox")
        val cursor: Cursor? = contentResolver.query(uriSms, arrayOf("_id", "address", "date", "body"), null, null, null)

        cursor!!.moveToFirst()
        while (cursor.moveToNext()) {
            val address: String = cursor.getString(1)
            val body: String = cursor.getString(3)

            if(body.contains("Debited")){
                println("======&gt; Mobile number =&gt; $address")
                println("=====&gt; SMS Text =&gt; $body")

                for (i in body.indices) {

                    val data = body as CharSequence
                    //String with Rs.
                    val regEx = Pattern.compile("(?:inr|Rs.)+[\\s]*[0-9+[\\,]*+[0-9]*]+[\\.]*[0-9]+")
                    val m: Matcher = regEx.matcher(data)
                    if (m.find()) {
                        Log.e("Test", "Amount" + m.group(0))
                        var amount: String = m.group(0).replace("inr", "")
                        amount = amount.replace("rs".toRegex(), "")
                        amount = amount.replace("inr".toRegex(), "")
                        amount = amount.replace(" ".toRegex(), "")
                        amount = amount.replace(",".toRegex(), "")

                        finalDebitAmount = amount.removeRange(0,3)
                        DebitList.add(finalDebitAmount)
                        Log.e("Test", "finalDebitAmount $DebitList")
                        break

                    }
                }
                DebitedList.add(SmsData(address, body))
            }
        }
        return DebitedList
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_report -> {

                var intent = Intent(this,ReportActivity::class.java)
                if(DebitList!=null){
                   intent.putExtra("DEBIT",DebitList)
                }
                if(CreditList!=null){
                    intent.putExtra("CREDIT",CreditList)
                }
                startActivity(intent)
                return true
            }
            R.id.action_help -> {
                var intent = Intent(this,HelpActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}


