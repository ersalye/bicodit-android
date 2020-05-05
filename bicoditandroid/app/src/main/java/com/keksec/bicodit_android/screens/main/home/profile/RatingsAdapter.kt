package com.keksec.bicodit_android.screens.main.home.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.keksec.bicodit_android.R
import com.keksec.bicodit_android.core.data.local.room.models.rating.RatingData
import com.mikhaellopez.circularimageview.CircularImageView
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import org.threeten.bp.temporal.ChronoField
import java.util.*

/**
 * This adapter is used to render all the user ratings into the recycler view of HomeFragment
 */
class RatingsAdapter(val context: Context, var items: List<RatingData>, val callback: (RatingData) -> Unit) : RecyclerView.Adapter<RatingsAdapter.RatingHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.rating_item,
            parent,
            false
        )
        return RatingHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RatingHolder, position: Int) {
        holder.bind(items[position], context)
    }

    fun setData(newData: List<RatingData>) {
        items = newData
        notifyDataSetChanged()
    }

    inner class RatingHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ratingAvatar = itemView.findViewById<CircularImageView>(R.id.ratingAvatar)
        private val ratingLogin = itemView.findViewById<TextView>(R.id.ratingLogin)
        private val ratingDate = itemView.findViewById<TextView>(R.id.ratingDate)
        private val ratingValue = itemView.findViewById<ImageView>(R.id.ratingValue)
        private val ratingImage = itemView.findViewById<ImageView>(R.id.ratingImage)
        private val ratingText = itemView.findViewById<TextView>(R.id.ratingText)

        fun bind(rating: RatingData, context: Context) {
            val avatarResourceId =
                context.resources.getIdentifier(rating.userAvatar, "drawable", context.packageName)
            ratingAvatar.setImageResource(avatarResourceId)
            ratingLogin.text = rating.userLogin
            val l = Locale("ru","RU")
            if (rating.createdTime.isBefore(OffsetDateTime.now().with(ChronoField.HOUR_OF_DAY, 0))) {
                ratingDate.text = rating.createdTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(l))
            } else {
                ratingDate.text = rating.createdTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(l))
            }
            val text = rating.text
            if (text.isEmpty()) {
                ratingText.text = "..."
            } else {
                ratingText.text = text
            }
            val ratingValueResourceId = getRatingValueResourceId(rating.value)
            ratingValue.setImageResource(ratingValueResourceId)
            val moodImageResourceId = getMoodResourceId(rating.value)
            ratingImage.setImageResource(moodImageResourceId)
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback(items[adapterPosition])
            }
        }

        private fun getRatingValueResourceId(value: Int): Int {
            return when (value) {
                1 -> R.drawable.one_focused
                2 -> R.drawable.two_focused
                3 -> R.drawable.three_focused
                4 -> R.drawable.four_focused
                5 -> R.drawable.five_focused
                else ->  -1
            }
        }

        private fun getMoodResourceId(value: Int): Int {
            return when (value) {
                1 -> R.drawable.super_sad_panda
                2 -> R.drawable.very_sad_panda
                3 -> R.drawable.ordinary_panda
                4 -> R.drawable.happy_panda
                5 -> R.drawable.super_happy_panda
                else ->  -1
            }
        }
    }
}