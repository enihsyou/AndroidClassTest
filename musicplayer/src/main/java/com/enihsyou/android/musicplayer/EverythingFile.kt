package com.enihsyou.android.musicplayer

import android.os.Parcel
import android.os.Parcelable

data class EverythingFile(
    val name: String,
    val path: String,
    val size: String,
    val time: String,
    val url: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(path)
        parcel.writeString(size)
        parcel.writeString(time)
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EverythingFile> {
        override fun createFromParcel(parcel: Parcel): EverythingFile {
            return EverythingFile(parcel)
        }

        override fun newArray(size: Int): Array<EverythingFile?> {
            return arrayOfNulls(size)
        }
    }
}
