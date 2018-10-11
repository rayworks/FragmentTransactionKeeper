# FragmentTransactionKeeper

A practical library for committing Fragment Transactions safely.

## The related issue:

```
java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
      at android.support.v4.app.FragmentManagerImpl.checkStateLoss(FragmentManager.java:2080)
      at android.support.v4.app.FragmentManagerImpl.enqueueAction(FragmentManager.java:2106)
      at android.support.v4.app.BackStackRecord.commitInternal(BackStackRecord.java:683)
      at android.support.v4.app.BackStackRecord.commit(BackStackRecord.java:637)
```

Also check : [Fragment Transactions & Activity State Loss](https://www.androiddesignpatterns.com/2013/08/fragment-transaction-commit-state-loss.html)


## Usage:

* Setup the [custom PlaceHolderFragment](./transactionkeeper/src/main/java/com/rayworks/transactionkeeper/PlaceHolderFragment) in your main UI (Activity / Fragment)

* Wrap your asynchronous Fragment transaction logic as below :
```
        placeHolderFragment.tryWithAction { activity ->
               kotlin.run {
                     // commit your own Framgment transaction here
               }
        }
```

* Done!

For detail info, please check the `MainActivity` in the `app` module.

## Credit:
[kshilovskiy's action-buffer](https://github.com/kshilovskiy/action-buffer)
provides an example of avoiding the IllegalStateException during the fragment transition while dealing with background tasks.


## Todo list:
- [ ] Refactor the library code to provide more convenient APIs