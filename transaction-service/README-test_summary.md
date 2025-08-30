## Test Summary

## ✅ DepositServiceTest
| Test Method                                                 | Purpose                                                                   |
| ----------------------------------------------------------- | ------------------------------------------------------------------------- |
| `deposit_shouldCreateTransactionSuccessfully()`             | Verifies a standard deposit transaction is processed and saved correctly. |
| `depositManual_shouldCreateManualTransactionSuccessfully()` | Tests manual admin deposit without fee and proper system gateway setup.   |
| `getTotalDeposit_shouldReturnCorrectAmount()`               | Checks the aggregation of all deposits for a user.                        |
| `isDepositExistsByTxnRef_shouldReturnTrueWhenExists()`      | Verifies detection of an existing deposit by transaction reference.       |
| `getDepositHistory_shouldReturnPagedResult()`               | Ensures paginated deposit history retrieval works as expected.            |
| `confirmGatewayDeposit_shouldUpdateStatusAndMetaInfo()`     | Confirms that a gateway deposit updates status and metadata properly.     |


## ✅ BonusTransactionServiceTest
| Test Method                                                 | Purpose                                                               |
| ----------------------------------------------------------- | --------------------------------------------------------------------- |
| `applySignupBonus_shouldCreateBonusTransaction()`           | Validates signup bonus transaction creation and balance update.       |
| `applyReferralBonus_shouldCreateReferralBonusTransaction()` | Ensures referral bonus is applied with correct metadata.              |
| `applyBonus_shouldCreateCustomBonusTransaction()`           | Confirms general-purpose bonus with dynamic reason works as intended. |
| `applyInterest_shouldCreateInterestTransaction()`           | Verifies interest crediting with metadata and period description.     |


## ✅ AdjustmentServiceTest
| Test Method                                             | Purpose                                                     |
| ------------------------------------------------------- | ----------------------------------------------------------- |
| `subtract_shouldCreateAdjustmentTransaction()`          | Checks balance reduction with proper transaction recording. |
| `subtract_shouldThrowExceptionIfAmountZeroOrNegative()` | Ensures illegal adjustment amounts throw an exception.      |
| `subtract_shouldThrowIfInsufficientBalance()`           | Verifies that subtraction fails if user lacks funds.        |


## ✅ TransactionQueryServiceTest
| Test Method                                     | Purpose                                                                |
| ----------------------------------------------- | ---------------------------------------------------------------------- |
| `searchTransactions_shouldReturnFilteredPage()` | Ensures dynamic filtering via `Specification` returns expected result. |
