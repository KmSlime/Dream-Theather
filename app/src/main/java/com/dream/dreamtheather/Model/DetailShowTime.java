package com.dream.dreamtheather.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Showtime chứa một mảng các ngày chiếu,
 * mỗi ngày chiếu chứa ngày chiếu, và mảng giờ chiếu
 * mỗi giờ chiếu chứa giờ chiếu, một phòng chiếu, giá vé và một mảng ghế là boolean thể hiện ghế còn trống hay không, số ghế mặc định là 25
 */
public class DetailShowTime {

    @SerializedName("time")
    private String mTime;
    @SerializedName("room")
    private int mRoom;
    @SerializedName("price")
    private int mPrice;
    @SerializedName("seats")
    private List<Boolean> mSeats;
    @SerializedName("seatRowNumber")
    private int mSeatRowNumber;

    @Override
    public String toString() {
        return "DetailShowTime{" +
                "mTime='" + mTime + '\'' +
                ", mRoom=" + mRoom +
                ", mPrice=" + mPrice +
                ", mSeats=" + mSeats +
                ", mSeatRowNumber=" + mSeatRowNumber +
                ", mSeatColumnNumber=" + mSeatColumnNumber +
                '}';
    }

    public int getSeatRowNumber() {
        return mSeatRowNumber;
    }

    public void setSeatRowNumber(int mSeatRowNumber) {
        this.mSeatRowNumber = mSeatRowNumber;
    }

    public int getSeatColumnNumber() {
        return mSeatColumnNumber;
    }

    public void setSeatColumnNumber(int mSeatColumnNumber) {
        this.mSeatColumnNumber = mSeatColumnNumber;
    }

    @SerializedName("seatColumnNumber")
    private int mSeatColumnNumber;

    public String getTime() {
        return mTime;
    }

    public void setTime(String mTime) {
        this.mTime = mTime;
    }

    public int getRoom() {
        return mRoom;
    }

    public void setRoom(int mRoom) {
        this.mRoom = mRoom;
    }

    public int getPrice() {
        return mPrice;
    }

    public void setPrice(int mPrice) {
        this.mPrice = mPrice;
    }

    public List<Boolean> getSeats() {
        return mSeats;
    }

    public void setSeats(List<Boolean> mSeats) {
        this.mSeats = mSeats;
    }


}
