package com.innova.checarsaldoapp.model;


public class SquareItem {
    private boolean active;

    private String color;

    private int image;

    private String qty;

    private String title;

    public SquareItem(int paramInt, String paramString1, String paramString2, String paramString3) {
        this.image = paramInt;
        this.title = paramString1;
        this.color = paramString3;
        this.qty = paramString2;
    }

    public SquareItem(int paramInt, String paramString1, String paramString2, boolean paramBoolean) {
        this.image = paramInt;
        this.title = paramString1;
        this.color = paramString2;
        this.active = paramBoolean;
    }

    public String getColor() {
        return this.color;
    }

    public int getImage() {
        return this.image;
    }

    public String getQty() {
        return this.qty;
    }

    public String getTitle() {
        return this.title;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean paramBoolean) {
        this.active = paramBoolean;
    }

    public void setColor(String paramString) {
        this.color = paramString;
    }

    public void setImage(int paramInt) {
        this.image = paramInt;
    }

    public void setQty(String paramString) {
        this.qty = paramString;
    }

    public void setTitle(String paramString) {
        this.title = paramString;
    }
}
