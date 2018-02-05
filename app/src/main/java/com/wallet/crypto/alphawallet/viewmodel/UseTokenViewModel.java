package com.wallet.crypto.alphawallet.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.Nullable;

import com.wallet.crypto.alphawallet.entity.NetworkInfo;
import com.wallet.crypto.alphawallet.entity.Ticket;
import com.wallet.crypto.alphawallet.entity.Wallet;
import com.wallet.crypto.alphawallet.interact.FindDefaultNetworkInteract;
import com.wallet.crypto.alphawallet.interact.FindDefaultWalletInteract;
import com.wallet.crypto.alphawallet.interact.SignatureGenerateInteract;
import com.wallet.crypto.alphawallet.interact.UseTokenInteract;
import com.wallet.crypto.alphawallet.router.MyTokensRouter;
import com.wallet.crypto.alphawallet.router.SignatureDisplayRouter;
import com.wallet.crypto.alphawallet.router.TicketTransferRouter;

import io.reactivex.disposables.Disposable;

/**
 * Created by James on 22/01/2018.
 */

public class UseTokenViewModel extends BaseViewModel {
    private static final long CYCLE_SIGNATURE_INTERVAL = 30;
    private final FindDefaultNetworkInteract findDefaultNetworkInteract;
    private final UseTokenInteract useTokenInteract;
    private final FindDefaultWalletInteract findDefaultWalletInteract;
    private final MyTokensRouter myTokensRouter;
    private final TicketTransferRouter ticketTransferRouter;
    private final SignatureGenerateInteract signatureGenerateInteract;
    private final SignatureDisplayRouter signatureDisplayRouter;

    private final MutableLiveData<NetworkInfo> defaultNetwork = new MutableLiveData<>();
    private final MutableLiveData<Wallet> defaultWallet = new MutableLiveData<>();
    private final MutableLiveData<String> signature = new MutableLiveData<>();

    @Nullable
    private Disposable cycleSignatureDisposable;

    UseTokenViewModel(
            UseTokenInteract useTokenInteract,
            FindDefaultWalletInteract findDefaultWalletInteract,
            SignatureGenerateInteract signatureGenerateInteract,
            MyTokensRouter myTokensRouter,
            TicketTransferRouter ticketTransferRouter,
            SignatureDisplayRouter signatureDisplayRouter,
            FindDefaultNetworkInteract findDefaultNetworkInteract) {
        this.useTokenInteract = useTokenInteract;
        this.findDefaultWalletInteract = findDefaultWalletInteract;
        this.myTokensRouter = myTokensRouter;
        this.findDefaultNetworkInteract = findDefaultNetworkInteract;
        this.signatureDisplayRouter = signatureDisplayRouter;
        this.signatureGenerateInteract = signatureGenerateInteract;
        this.ticketTransferRouter = ticketTransferRouter;
    }

    public LiveData<Wallet> defaultWallet() {
        return defaultWallet;
    }
    public LiveData<String> signature() {
        return signature;
    }

    public void showRotatingSignature(Context context, Ticket token) {
        signatureDisplayRouter.open(context, defaultWallet.getValue(), token);

    }

    public void prepare() {
        progress.postValue(true);
        disposable = findDefaultNetworkInteract
                .find()
                .subscribe(this::onDefaultNetwork, this::onError);
    }

    private void onDefaultNetwork(NetworkInfo networkInfo) {
        defaultNetwork.postValue(networkInfo);
        disposable = findDefaultWalletInteract
                .find()
                .subscribe(this::onDefaultWallet, this::onError);
    }

    public void showTransferToken(Context context, Ticket ticket) {
        ticketTransferRouter.open(context, ticket);
    }

    private void onDefaultWallet(Wallet wallet) {
        //TODO: switch on 'use' button
        progress.postValue(false);
        defaultWallet.setValue(wallet);
    }
}