package vanwingerdenbarrier.sheetmusictutor.Quiz;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Random;

import vanwingerdenbarrier.sheetmusictutor.Game.QuestionDisplay;
import vanwingerdenbarrier.sheetmusictutor.R;
import vanwingerdenbarrier.sheetmusictutor.StaffStructure.DrawStaff;

/**
 * Fragment that displays quiz questions and contains the correct answer
 */
public class QuizQuestionFragment extends Fragment implements QuestionDisplay{

    Display callback;
    ViewGroup view;
    QuestionStorage qS;
    String correctAnswer;
    Random random;
    int currentScore;
    int currentLives;

    /**This Hash Set will hold question numbers that have already been used to avoid duplicate questions*/
    static HashSet<Integer> used = new HashSet<>();

    /**Index of the current question*/
    int questionIndex;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity();

        qS = new QuestionStorage();
        qS.initialQuestions(this.getContext());

        Bundle args = getArguments();
        currentLives = args.getInt("lives");
        currentScore = args.getInt("score");

        view = (ViewGroup) inflater.inflate(R.layout.fragment_quiz_question,
                container, false);

        TextView question = (TextView) view.findViewById(R.id.questionString);

        random = new Random();

        if(used.size() == qS.getLength())//clear hash set if all questions have been used
            used.clear();

        //Add question index to used hashSet if already used question
        questionIndex = random.nextInt(qS.getLength());
        while(used.contains(questionIndex)){
            questionIndex = random.nextInt(qS.getLength());
        }
        used.add(questionIndex);



        question.setText(qS.getQuestion(questionIndex));
        correctAnswer = qS.getCorrectAnswer(questionIndex);

        callback.questionPressed(questionIndex, 0, 0);

        return view;
    }

    /**
     * checks if a given String is the correct answer to the current problem
     * @param answer the answer to check
     */
    public void checkIfCorrect(String answer){

        if(answer.equals(correctAnswer)){
            getView().performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            Toast.makeText(this.getContext(),"Correct", Toast.LENGTH_SHORT).show();
            callback.questionPressed(null, currentScore++, currentLives);

        }else{
            getView().performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            getView().performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            Toast.makeText(this.getContext(),"Incorrect, Try Again", Toast.LENGTH_SHORT).show();
        }

    }


    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            callback = (Display) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
}
