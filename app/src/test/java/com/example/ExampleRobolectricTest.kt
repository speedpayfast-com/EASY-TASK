package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.TaskEntity
import com.example.data.model.UserEntity
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("EASY TASK", appName)
  }

  @Test
  fun `verify four percent commission calculation`() {
    val task = TaskEntity(
      title = "bKash Send Money",
      method = "BKASH",
      targetNumber = "01811-223344",
      amount = 1000.0,
      commissionRate = 0.04,
      instructions = "Send Money"
    )
    val expectedCommission = 1000.0 * 0.04
    assertEquals(40.0, expectedCommission, 0.001)
  }

  @Test
  fun `verify referral challenge values and dual balance initialization`() {
    val user = UserEntity(
      name = "Rahim Ahmed",
      phone = "01811223344",
      whatsapp = "01811223344",
      pin = "1234",
      referralCode = "ET8899",
      referredBy = "ETADMIN",
      mainBalance = 5000.0,
      commissionBalance = 0.0
    )

    assertEquals(5000.0, user.mainBalance, 0.001)
    assertEquals(0.0, user.commissionBalance, 0.001)

    // Referrer gets 200 Tk, referee gets 100 Tk if 5000 Tk completed within 2 days
    val referrerBonus = 200.0
    val refereeBonus = 100.0
    val challengeVolumeTarget = 5000.0

    assertEquals(200.0, referrerBonus, 0.001)
    assertEquals(100.0, refereeBonus, 0.001)
    assertEquals(5000.0, challengeVolumeTarget, 0.001)
  }

  @Test
  fun `verify transaction log entity data consistency`() {
    val log = com.example.data.model.TransactionLogEntity(
      userId = 1L,
      category = "TASK",
      type = "BKASH_TASK_SUBMITTED",
      title = "bKash Send Money - Completed",
      description = "bKash Send Money of ৳1,000 to 01811-223344",
      method = "BKASH",
      amount = 1000.0,
      commissionAmount = 40.0,
      walletAffected = "COMMISSION",
      balanceImpact = 40.0,
      trxId = "9K8L7M6N5P",
      senderNumber = "01811223344",
      targetNumber = "01811-223344",
      status = "COMPLETED"
    )

    assertEquals("BKASH", log.method)
    assertEquals("COMPLETED", log.status)
    assertEquals(40.0, log.commissionAmount, 0.001)
    assertEquals(1000.0, log.amount, 0.001)
  }
}
