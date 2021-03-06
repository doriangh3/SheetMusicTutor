package vanwingerdenbarrier.sheetmusictutor.Quiz;

import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import vanwingerdenbarrier.sheetmusictutor.R;
import vanwingerdenbarrier.sheetmusictutor.ResultsActivity;
import vanwingerdenbarrier.sheetmusictutor.UserInfo.User;
import vanwingerdenbarrier.sheetmusictutor.UserInfo.UserList;

public class QuizActivity extends AppCompatActivity {

    private QuestionStorage questionLibrary = new QuestionStorage();

    private final int MAX_ATTEMPTS = 1;

    /**Attach these variables to XML Buttons*/
    private TextView scoreView;
    private TextView questionView;
    private Button buttonChoice1;
    private Button buttonChoice2;
    private Button buttonChoice3;

    UserList userList;
    User current;

    private boolean isQuiz = true;

    /**Use to compare user answer to correct answer*/
    private String answer;

    /**Most possible points you can get*/
    private int pointsPossible = 0;

    /**Score based on number of attempts*/
    private float percentage;

    /**Number of questions*/
    private int numQuestions;

    /**Question difficulty*/
    private int difficulty;

    /**Number of questions correct*/
    private int correct;

    /**Keep track of users current score*/
    private int score = 0;

    /**Keeps track of the current question number*/
    private int questionNumber = 0;

    /**True if user has not leveled up during a quiz session. False if they have leveled up*/
    boolean levelUp = true;


    /**
     * This listener holds the logic for the quiz.
     * Updates the score, points needed to level up,
     */
    private View.OnClickListener vl = new View.OnClickListener(){
        @Override
        public void onClick(View view){

            if (view instanceof  Button) {
                Button button = (Button) view;

                //If correct answers is chosen
                if (button.getText().equals(answer)) {
                    score += difficulty;
                    updateScore(score);
                    correct++;
                    userList.addUserCorrect();
                    userList.addUserAttempt();

                    //This if levels up the user if they have reached the number of points needed
                    if(current.getNumPointsNeeded() == current.getNumQuestionsCorrect()  && levelUp == true){
                        levelUp = false;//So that we don't level up the user more than once by mistake
                        userList.levelUpUser();
                        userList.addUserPointsNeeded();//increment points needed to level up
                    }

                    Toast.makeText(QuizActivity.this, "Correct!", Toast.LENGTH_SHORT).show();

                    //If not the last question
                    if(questionNumber < numQuestions) {
                        updateQuestion();
                    }
                    else if(questionNumber == numQuestions){//If is the last question. Send stats to quiz results screen

                        Intent results = new Intent(view.getContext(), ResultsActivity.class);

                        percentage = ( (float) score/ (float) pointsPossible)*100;

                        results.putExtra("percent",(int) percentage);
                        results.putExtra("numQuestions",numQuestions);
                        results.putExtra("correct",correct);
                        results.putExtra("score",score);
                        results.putExtra("points",pointsPossible);
                        results.putExtra("isQuiz",isQuiz);

                        results.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivityForResult(results,0);
                    }

                } else {//Incorrect answer chosen

                    //Decrement difficulty which corresponds to the number of points a user will receive
                    //In other words the more questions you get wrong the less points a user gets
                    if(difficulty > MAX_ATTEMPTS){
                        difficulty--;
                        Toast.makeText(QuizActivity.this, "Wrong!", Toast.LENGTH_SHORT).show();
                    }
                    //If you have reached max number of attempts for a question
                    else if(difficulty == MAX_ATTEMPTS){
                        Toast.makeText(QuizActivity.this, "No More Attempts!", Toast.LENGTH_SHORT).show();
                        if(questionNumber < numQuestions) {//if not the last question
                            userList.addUserAttempt();
                            updateQuestion();
                        }
                        else{//if the last question pass the results to the stats screen

                            Intent results = new Intent(view.getContext(), ResultsActivity.class);

                            percentage = ( (float) score/ (float) pointsPossible)*100;

                            Toast.makeText(QuizActivity.this, "Percent: "+numQuestions, Toast.LENGTH_SHORT).show();

                            results.putExtra("percent",(int) percentage);
                            results.putExtra("numQuestions",numQuestions);
                            results.putExtra("correct",correct);
                            results.putExtra("score",score);
                            results.putExtra("points",pointsPossible);
                            results.putExtra("isQuiz",isQuiz);

                            startActivityForResult(results,0);

                        }
                    }

                }
            }
        }
    };

    /**
     * Set up all initial fields to be updated when a new question is drawn t the display
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setVolumeControlStream(AudioManager.STREAM_MUSIC);//set media volume control

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        scoreView = (TextView)findViewById(R.id.score);
        questionView = (TextView)findViewById(R.id.question);
        buttonChoice1 = (Button)findViewById(R.id.choice1);
        buttonChoice2 = (Button)findViewById(R.id.choice2);
        buttonChoice3 = (Button)findViewById(R.id.choice3);

        userList = new UserList(this);
        current = new UserList(getBaseContext()).findCurrent();

        questionLibrary.initialQuestions(getApplicationContext());
        numQuestions = questionLibrary.list.size();
        updateQuestion();//??To get initial question from array to display
        updateScore(score);

        //Start button listener for Button1
        buttonChoice1.setOnClickListener(vl);
        //End button listener for Button1

        //Start button listener for Button2
        buttonChoice2.setOnClickListener(vl);
        //End button listener for Button2

        //Start button listener for Button3
        buttonChoice3.setOnClickListener(vl);
        //End button listener for Button3
    }

    /**
     * Updates the questionNumber and corresponding question and answers
     * Updates text in xml
     */
    public void updateQuestion(){

        questionView.setText(questionLibrary.getQuestion(questionNumber));
        buttonChoice1.setText(questionLibrary.getChoice(questionNumber,1));
        buttonChoice2.setText(questionLibrary.getChoice(questionNumber,2));
        buttonChoice3.setText(questionLibrary.getChoice(questionNumber,3));

        String diff = questionLibrary.getDifficultyScore(questionNumber);
        difficulty = Integer.parseInt(diff);
       //adds difficulty to number of possible points
        pointsPossible+=difficulty;
        answer = questionLibrary.getCorrectAnswer(questionNumber);
        questionNumber++;
    }

    /**
     * Updates the current score
     * @param point or score
     */
    public void updateScore(int point){
        scoreView.setText("" + score);
    }



}//end Class QuizActivity


