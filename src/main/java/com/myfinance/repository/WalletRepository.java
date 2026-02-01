package com.myfinance.repository;

import com.myfinance.model.Wallet;
import com.myfinance.util.FileStorage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WalletRepository {
    private Map<String, Wallet> wallets;

    public WalletRepository() {
        this.wallets = new HashMap<>();
    }

    public Wallet findById(String id) {
        // Пытаемся загрузить из файла, если нет в памяти
        if (!wallets.containsKey(id)) {
            try {
                Wallet wallet = FileStorage.loadWallet(id);
                if (wallet != null) {
                    wallets.put(id, wallet);
                }
            } catch (IOException e) {
                System.err.println("Ошибка загрузки кошелька " + id + ": " + e.getMessage());
            }
        }
        return wallets.get(id);
    }

    public Wallet findByUserId(String userId) {
        String walletId = userId + "_wallet";
        return findById(walletId);
    }

    public void save(Wallet wallet) throws IOException {
        wallets.put(wallet.getId(), wallet);
        FileStorage.saveWallet(wallet);
    }

    public void delete(String id) {
        wallets.remove(id);
        // Также удаляем файл
        java.io.File file = new java.io.File("data/wallets/" + id + ".txt");
        if (file.exists()) {
            file.delete();
        }
    }

    public Map<String, Wallet> getAllWallets() {
        return wallets;
    }
}