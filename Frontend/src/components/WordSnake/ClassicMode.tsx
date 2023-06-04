import "./ClassicMode.css";

import { withFuncProps } from "../withFuncProps";
import { logout, getLetterFromPreviousWord, getRandomStart, getHintWordAndDef } from '../../helpers/connector';
import { TextField, FormHelperText } from "@mui/material";

import snake_whole from "../../images/snake_whole.png";

import React from "react";
import CountdownTimer from "./CountdownTimer";
import HintPopup from "./HintPopupProps";

class ClassicMode extends React.Component<any, any>{
    constructor(props: any) {
        super(props);
        this.state = {
            isGameStarted: false,
            ForceUpdateNow: false, 
            isGameOver: false, showWords: true, 
            printHints: [], showHints: false,
            lastWord:"", lastLetter: "", firstWord: "", 
            inputValue: '',
            storedInputValue: '', inputValidString: '',
            errMessage: '', 
            timeLeft: 60, wordList: [], history: [],
            snakeGrowthLevel: 1,
            snakeSize: 300, //width of snake to track the position
            sideNavWidth: 120,
            topNavHeight:43,
            snakeDirection: 'right',
            snakePosition: {x:0, y:0},
            containerSize: {width:window.innerWidth, height: window.innerHeight}
        };
        this.menuNav = this.menuNav.bind(this);
        this.moveSnake = this.moveSnake.bind(this);
    }

    moveSnake() {
        let {snakeDirection, snakePosition, snakeSize, containerSize, sideNavWidth, topNavHeight} = this.state;
        let newSnakePosition = {...snakePosition};
        let dir = ""

        switch(snakeDirection) {
            case 'right':
                newSnakePosition.x += snakeSize;

                console.log("inside right")
                if (newSnakePosition.x >= containerSize.width - sideNavWidth) {
                    dir = 'down';
                    this.setState({ snakeDirection: dir })
                }
                break;
            case 'down':
                newSnakePosition.y += snakeSize;
                if (newSnakePosition.y >= containerSize.height - topNavHeight) {
                    dir = 'left';
                    this.setState({ snakeDirection: dir })
                }
                break;
            case 'left':
                newSnakePosition.x -= snakeSize; 
                if (newSnakePosition.x <= 0) {
                    dir = 'up';
                    this.setState({ snakeDirection: dir })
                }
                break;
            case 'up':
                newSnakePosition.y -= snakeSize;
                if (newSnakePosition.y <= 0) {
                    dir = 'right';
                    this.setState({ snakeDirection: dir })
                }
                break;
            default:
                break;
        }
        console.log("containerSize.width - sideNavWidth", containerSize.width - sideNavWidth)
        console.log("containerSize.width", containerSize.width)

        console.log("newSnakePosition.x:",newSnakePosition.x)
        console.log("newSnakePosition.y:",newSnakePosition.y)

        
        this.setState({snakePosition: newSnakePosition});
    }

    getSnakeWholeClass() {
        const { snakeDirection } = this.state;
        switch(snakeDirection) {
            case 'right':
                return "snakeWholeRight";
            case 'down':
                return "snakeWholeDown";
            case 'left':
                return "snakeWholeLeft";
            case 'up':
                return "snakeWholeUp";
            default:
                return "";
        }
    }

    forceup = async (inputValue: string) => {
        try {
            if (this.state.wordList.includes(inputValue)) {
                this.setState({ errMessage: 'The word already exist. Please type another word.', inputValue: "", storedInputValue: "" })
            } else {
                const lastWord = this.state.wordList[this.state.wordList.length - 1]
                const lastLetter = lastWord[lastWord.length - 1]
                if (inputValue[0] === lastLetter) {
                    const words = await getLetterFromPreviousWord(inputValue);
                    let wordList = this.state.wordList.concat(inputValue);

                    if (this.state.showHints){
                        this.handleCloseHint();
                    }
                    
                    this.moveSnake();

                    this.setState({
                        lastWord: lastWord,
                        errMessage: '',
                        firstWord: words,
                        ForceUpdateNow: false,
                        wordList: wordList,
                    });
                    
                    let hisArr = this.state.history.concat(inputValue);
                    const lastWordForHint = hisArr[hisArr.length - 1]
                    const lastLetter = lastWordForHint[lastWordForHint.length - 1]
                    
                    this.setState({history: hisArr, lastLetter: lastLetter})
                } else {
                    this.setState({ errMessage: `The word must start with '${lastLetter}'`, inputValue: "", storedInputValue: "" })
                }
            }
        } catch (error) {
            console.error("Error fetching word in the database:", error);
            this.setState({ errMessage: 'The word does not exist. Please enter a valid word.', inputValue: "", storedInputValue: "" });
        }
        
    };

    handleTimeUp = () => {
        this.updateGameState(false, true)
    };

    handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const inputString = event.target.value;
        if (
            inputString.startsWith('-') || 
            inputString.startsWith('\''))
        {
            this.setState({ 
                errMessage: 'Apostrophes and/or hyphens cannot be used in the beginning of a word.' 
            });
        } 
        else if (inputString === "") {
            this.setState({
                inputValue: "",
                errMessage: ""
            });
        } else {
            const isValid = /^[a-zA-Z'-]*$/.test(inputString);

            if (isValid) {
                this.setState({
                    inputValue: inputString,
                    errMessage: ""
                });
            } else {
                this.setState({ errMessage: 'Special character(s) or number(s) are not accepted (except apostrophes, hyphens).' })
            }
        }
    }

    handleEnterKeyDown = (event: React.KeyboardEvent<HTMLInputElement>) => {
        if (event.key === "Enter") {
            this.storeInputValue(this.state.inputValue).then(() => {
                this.setState({ inputValue: "" });
            });
        }
    }

    storeInputValue = async (inputValue: string) => {
        try {
            if (inputValue !== this.state.storedInputValue) {
                if (inputValue.endsWith('\'') || inputValue.endsWith('-')){
                    this.setState({ 
                        errMessage: 'Apostrophes and/or hyphens cannot be used in the ending of a word.' 
                    });
                }
                else{
                    const lowerInput = inputValue.toLowerCase();
                    this.setState({ storedInputValue: lowerInput, ForceUpdateNow: true })
                    this.forceup(lowerInput);
                }
            }
        } catch (error) {
            console.error(error)
        }
    }

    reStart = () => {
        window.location.reload();
    }

    menuNav = () => {
        this.props.navigate("/menu")
    }

    pagelogout = () => {
        logout().then(() => {
            this.props.navigate("/")
        }).catch(() => (alert("logout error")));
    }

    updateGameState = async (isGameStarted: boolean, isGameOver: boolean) => {
        if (isGameStarted) {
            const fWord = await getRandomStart();
            this.setState({ 
                isGameStarted: true, isGameOver: false, 
                wordList: this.state.wordList.concat(fWord), 
                firstWord: fWord, 
                lastLetter: fWord 
            });
        }

        if (isGameOver) {
            this.setState({ 
                isGameStarted: false, 
                isGameOver: true, 
                wordList: [], 
                errMessage: "" 
            })
            this.props.navigate("/ResultListFunc", {
                state: {
                  wordList: this.state.history
                }
              })
        }
    }

    handleShowWords = () => {
        this.setState({
            showWords: !this.state.showWords
        })
    }

    handleGiveHints = async() => {
        const hints = await getHintWordAndDef(this.state.lastLetter);

        this.setState({
            showHints: !this.state.showHints,
            printHints: hints
        })
    }

    handleCloseHint = () => {
        this.setState({ showHints: false, printHints: []})
    }

    render() {
        const { firstWord, inputValue, wordList, errMessage, 
            isGameStarted, showWords, printHints, showHints, 
            snakePosition
        } = this.state;
        const wordListWithoutFirst = wordList.slice(1);
        const sortedWords = [...wordListWithoutFirst].sort();

          console.log("snakePosition: ",snakePosition);

        return (
            <div className="App">
                <div className="topnav">
                    <button className="topnavButton" onClick={this.reStart} hidden={isGameStarted ? false : true}>Restart</button>
                    <button className="topnavButton" onClick={this.menuNav}>Menu</button>
                    <button className="topnavButton" onClick={this.pagelogout}>Logout</button>
                </div>
                {isGameStarted ? (
                    <div className="sidenav">
                    <button className="sidenavButton" onClick={this.handleShowWords}>{showWords ? 'Hide Words' : 'Show Words'}</button>
                    <button className="sidenavButton" onClick={this.handleGiveHints}>Hint</button>
                    {showHints && <HintPopup hint={printHints} onClose={this.handleCloseHint} />}
                </div>
                ) : null}

                {isGameStarted? (
                    <div className="snakeContainer" style={{ left: snakePosition.x, top: snakePosition.y }}>
                        <img className={`snakeWhole ${this.getSnakeWholeClass()}`} src={snake_whole} alt="Snake Whole" />
                    </div>
                    
                ) : null}
            
                <h1 className="wsTitle">Word Snake</h1>
                {isGameStarted ? (
                    <CountdownTimer duration={300} onTimeUp={this.handleTimeUp}/>
                ) : (
                    <button className="topnavButton" onClick={() => this.updateGameState(true, false)} hidden={isGameStarted ? true : false}>Start Game</button>
                )}

                <div>
                    <TextField
                        label={`Enter a word starts with '${firstWord}'`}
                        value={inputValue}
                        onChange={this.handleInputChange}
                        onKeyDown={this.handleEnterKeyDown}
                        style={{
                            display: isGameStarted ? 'block' : 'none'
                        }}

                    />
                </div>
                <div>
                    <FormHelperText style={{ color: 'red' }}>
                        {errMessage}
                    </FormHelperText>
                </div>

                {showWords && sortedWords.length > 0 && (
                    <div className="container">
                        {sortedWords.map((word: string, index: number) => (
                            <li key={index}>{word}</li>
                        ))}
                    </div>
                )}
            </div>
        );
    }
}


export default withFuncProps(ClassicMode);