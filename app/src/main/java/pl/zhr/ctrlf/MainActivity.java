package pl.zhr.ctrlf;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.textViewPhrases) TextView textViewPhrases;
    @BindView(R.id.textViewText) TextView textViewText;
    private String phrase = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    /**
     * COPY TEXT FROM CLIPBOARD button - sets text from clipboard on TextViewText below
     */
    @OnClick(R.id.buttonCopy)
    public void setButtonCopy() {
        setText();
    }

    /**
     * FIND button - opens Dialog for entering the phrase and marks the fragments found
     */
    @OnClick(R.id.buttonFind)
    public void setButtonFind() {
        // if original text hasn't changed, don't go on
        if (textViewText.getText().toString().equals(getString(R.string.place))) {
            Toast.makeText(this, getString(R.string.first_copy), Toast.LENGTH_SHORT).show();
        } else {
            givePhrase().show();
        }
    }

    /**
     * RESET button - resets all TextViews
     */
    @OnClick(R.id.buttonReset)
    public void setButtonReset() {
        textViewText.setText(getString(R.string.place));
        phrase = "";
        textViewPhrases.setText("");
    }

    /**
     * Dialog with EditText - gets the phrase
     * @return Dialog to show
     */
    private Dialog givePhrase() {
        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(getString(R.string.find));
        dialogBuilder.setView(input);

        dialogBuilder.setNegativeButton(getString(R.string.cancel), (dialog, whichButton) -> {
            // do nothing
        });

        dialogBuilder.setPositiveButton(getString(R.string.ok), (dialog, whichButton) -> {
            // check if input is not empty!
            if (!input.getText().toString().isEmpty()) {
                phrase = input.getText().toString();
                findPhrase(phrase);
            } else {
                setText();
                textViewPhrases.setText("");
            }
        });

        return dialogBuilder.create();
    }

    /**
     * Marks all found phrases
     * @param phrase phrase from Dialog's EditText
     */
    private void findPhrase(String phrase) {
        // counter for local repeats
        int counter = 0;
        // counter for whole phrases
        int globalCounter = 0;
        // buffer for indexes
        int buffer = 0;
        String text = textViewText.getText().toString();
        SpannableString spannableString = new SpannableString(text);
        text = text.toLowerCase();
        phrase = phrase.toLowerCase();

        for (int i = 0; i < text.length(); i++) {
            // go on if the next letter matches
            if (phrase.charAt(counter) == text.charAt(i)) {
                counter++;
                // go on if this is the end of the phrase
                if (counter == phrase.length()) {
                    spannableString.setSpan(new BackgroundColorSpan(Color.YELLOW), buffer, buffer + counter, 0);
                    spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), buffer, buffer + counter, 0);
                    counter = 0;
                    globalCounter++;
                }
            } else {
                buffer = i + 1;
                counter = 0;
            }
        }
        textViewText.setText(spannableString);
        textViewPhrases.setText(getString(R.string.found, globalCounter));
    }

    /**
     * Sets TextView under the line
     */
    private void setText() {
        final android.content.ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = Objects.requireNonNull(clipboardManager).getPrimaryClip();
        int itemCount = Objects.requireNonNull(clipData).getItemCount();

        // chceck if clipboard is not empty
        if (itemCount > 0) {
            // set text on textview under the line
            textViewText.setText(clipData.getItemAt(0).getText().toString());
        }
    }
}
