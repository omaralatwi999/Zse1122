package com.custom.gametranslator;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_OVERLAY_PERMISSION = 1001;

    private TextView tvStatus;
    private EditText etEnglishText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = findViewById(R.id.tvStatus);
        etEnglishText = findViewById(R.id.etEnglishText);

        Button btnOverlayPermission = findViewById(R.id.btnOverlayPermission);
        Button btnAccessibility = findViewById(R.id.btnAccessibility);
        Button btnShowTranslation = findViewById(R.id.btnShowTranslation);
        Button btnHideOverlay = findViewById(R.id.btnHideOverlay);

        btnOverlayPermission.setOnClickListener(v -> requestOverlayPermission());

        btnAccessibility.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "تعذر فتح إعدادات تسهيل الاستخدام", Toast.LENGTH_SHORT).show();
            }
        });

        btnShowTranslation.setOnClickListener(v -> {
            String rawText = etEnglishText.getText().toString();
            String translated = SimpleTranslator.translateToArabic(rawText);

            if (canDrawOverlays()) {
                Intent serviceIntent = new Intent(this, OverlayService.class);
                serviceIntent.putExtra(OverlayService.EXTRA_TEXT, translated);
                startService(serviceIntent);
                tvStatus.setText("الحالة: تم عرض الترجمة فوق الشاشة");
            } else {
                tvStatus.setText("الحالة: العرض فوق التطبيقات مقيد — جرّب تفعيل تسهيل الاستخدام أو التبديل لوضع النص المباشر");
                Toast.makeText(this, translated, Toast.LENGTH_LONG).show();
            }
        });

        btnHideOverlay.setOnClickListener(v -> {
            stopService(new Intent(this, OverlayService.class));
            tvStatus.setText("الحالة: تم إخفاء نافذة الترجمة");
        });

        updateStatusText();
    }

    private boolean canDrawOverlays() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(this);
        }
        return true;
    }

    private void requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            try {
                Intent intent = new Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName())
                );
                startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION);
            } catch (Exception e) {
                Toast.makeText(this, "الجهاز لا يدعم فتح صلاحية العرض فوق التطبيقات", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "الصلاحية مفعلة مسبقاً", Toast.LENGTH_SHORT).show();
        }
        updateStatusText();
    }

    private void updateStatusText() {
        if (canDrawOverlays()) {
            tvStatus.setText("الحالة: صلاحية العرض فوق التطبيقات متاحة");
        } else {
            tvStatus.setText("الحالة: صلاحية العرض فوق التطبيقات غير مفعلة أو محظورة");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatusText();
    }
}
