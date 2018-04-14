package io.github.wulkanowy.data.db.dao.migrations;

import android.os.AsyncTask;

import org.greenrobot.greendao.database.Database;

import java.util.List;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.data.db.dao.DbHelper;
import io.github.wulkanowy.data.db.dao.entities.Account;
import io.github.wulkanowy.data.db.dao.entities.AccountDao;
import io.github.wulkanowy.data.db.dao.entities.DaoSession;
import io.github.wulkanowy.data.db.dao.entities.Diary;
import io.github.wulkanowy.data.db.dao.entities.DiaryDao;
import io.github.wulkanowy.data.db.shared.SharedPrefContract;
import io.github.wulkanowy.utils.DataObjectConverter;
import io.github.wulkanowy.utils.security.Scrambler;

public class Migration23 implements DbHelper.Migration {

    @Override
    public Integer getVersion() {
        return 23;
    }

    @Override
    public void runMigration(Database db, final SharedPrefContract sharedPref, final DaoSession daoSession, final Vulcan vulcan) throws Exception {
        DiaryDao.createTable(db, true);

        db.execSQL("DROP TABLE IF EXISTS tmp_account");
        db.execSQL("ALTER TABLE " + AccountDao.TABLENAME + " RENAME TO tmp_account");
        AccountDao.createTable(db, true);
        db.execSQL("INSERT INTO " + AccountDao.TABLENAME + "(NAME, E_MAIL, PASSWORD, SYMBOL, SCHOOL_ID) SELECT `NAME`, `E-MAIL`, `PASSWORD`, `SYMBOL`, `SNPID` FROM tmp_account");
        db.execSQL("DROP TABLE tmp_account");

        final Account account = daoSession.getAccountDao().load(sharedPref.getCurrentUserId());

        vulcan.setCredentials(
                account.getEmail(),
                Scrambler.decrypt(account.getEmail(), account.getPassword()),
                account.getSymbol(),
                account.getSchoolId(),
                "",
                ""
        );

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Diary> diaryList = DataObjectConverter.diariesToDiaryEntities(vulcan.getStudentAndParent().getDiaries());
                    daoSession.getDiaryDao().insertInTx(diaryList);

                    account.setRealId(vulcan.getStudentAndParent().getStudentID());
                    account.setSchoolId(vulcan.getStudentAndParent().getSchoolID());
                    daoSession.getAccountDao().save(account);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
